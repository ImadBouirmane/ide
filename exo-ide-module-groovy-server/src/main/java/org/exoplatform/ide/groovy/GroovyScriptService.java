/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.exoplatform.ide.groovy;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.jcr.ext.resource.jcr.Handler;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoader;
import org.exoplatform.services.jcr.ext.script.groovy.NodeScriptKey;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.ext.groovy.ResourceId;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.impl.ResourcePublicationException;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

import java.io.InputStream;
import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/groovy/")
public class GroovyScriptService extends GroovyScript2RestLoader
{
   
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(GroovyScriptService.class);

   public static final String DEVELOPER_ID = "ide.developer.id";
   
   public static final String WEBDAV_CONTEXT = "/ide-vfs-webdav/";

   /**
    * Resource live time. Resource will be expired after this if it is deployed
    * by user under 'developer' role.
    */
   int resourceLiveTime = 60 * 15 * 1000;

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, Handler jcrUrlHandler, InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         jcrUrlHandler, params);
   }

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService, GroovyJaxrsPublisher groovyPublisher,
      Handler jcrUrlHandler, InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         registryService, groovyPublisher, jcrUrlHandler, params);
   }

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService, Handler jcrUrlHandler,
      InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         registryService, jcrUrlHandler, params);
   }

   
   
   /**
    * Validate groovy script.
    * 
    * @param location location of groovy script
    * @param inputStream script for validation
    * @return {@link Response}
    */
   @POST
   @Path("/validate")
   public Response validate(@HeaderParam("location") String location, InputStream inputStream)
   {
      return super.validateScript(location, inputStream);
   }
   
   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/deploy-sandbox")
   @RolesAllowed({"developers"})
   public Response deployInSandbox(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      @Context SecurityContext security, MultivaluedMap<String, String> properties)
   {
      return sandboxLoader(uriInfo, location, true, security, properties);
   }
   
   
   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/undeploy-sandbox")
   @RolesAllowed({"developers"})
   public Response undeployFromSandox(@Context UriInfo uriInfo, @HeaderParam("location") String location,
       @Context SecurityContext security,  MultivaluedMap<String, String> properties)
   {
      return sandboxLoader(uriInfo, location, false, security, properties);
   }
   
   
   
   /**
    * Deploy groovy script as REST service. 
    * 
    * @param uriInfo URI information
    * @param location location of groovy script to be deployed
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/deploy")
   @RolesAllowed({"administrators"})
   public Response deploy(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }
      return super.load(jcrLocation[0], jcrLocation[1], jcrLocation[2], true, properties);
   }
   
   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/undeploy")
   @RolesAllowed({"administrators"})
   public Response undeploy(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);

      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }

      return super.load(jcrLocation[0], jcrLocation[1], jcrLocation[2], false, properties);
   }
   
   
   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param state if true - deploy, false - undeploy
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   private Response sandboxLoader(UriInfo uriInfo, String location,boolean state, SecurityContext security, MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }
      String userId = null;
      Principal principal = security.getUserPrincipal();
      if (principal != null)
      {
         userId = principal.getName();
      }
      if (userId == null)
      {
         // Should not happen
         return Response.status(Response.Status.FORBIDDEN).entity("User principal not found.").type(
            MediaType.TEXT_PLAIN).build();
      }

      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(jcrLocation[1],
               repositoryService.getRepository(jcrLocation[0]));
         Node script = ((Node)ses.getItem("/" + jcrLocation[2])).getNode("jcr:content");
         ResourceId key = new NodeScriptKey(jcrLocation[0], jcrLocation[1], script);

         ObjectFactory<AbstractResourceDescriptor> resource = groovyPublisher.getResource(key);
         if (resource != null)
         {
            String developer = resource.getObjectModel().getProperties().getFirst(DEVELOPER_ID);
            if (!userId.equals(developer))
            {
               return Response.status(Response.Status.FORBIDDEN).entity("Access to not own resource forbidden. ").type(
                  MediaType.TEXT_PLAIN).build();
            }
            groovyPublisher.unpublishResource(key);
         }
         else if (!state)
         {
            return Response.status(Response.Status.BAD_REQUEST).entity(
               "Can't remove resource " + jcrLocation[2] + ", not bound or has wrong mapping to the resource. ").type(
               MediaType.TEXT_PLAIN).build();
         }
         if (state)
         {
            if (properties == null)
            {
               properties = new MultivaluedMapImpl();
            }

            properties.putSingle(DEVELOPER_ID, userId);
            properties.putSingle(ResourceBinder.RESOURCE_EXPIRED, Long.toString(System.currentTimeMillis()
               + resourceLiveTime));
            groovyPublisher.publishPerRequest(script.getProperty("jcr:data").getStream(), key, properties);
         }

         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + jcrLocation[2] + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
      }
      catch (ResourcePublicationException e)
      {
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }

   }

   /**
    * @param baseUri base URI
    * @param location location of groovy script
    * @return array of {@link String}, which elements contain repository name, workspace name and 
    * path the path to JCR node that contains groovy script to be deployed
    */
   private String[] parseJcrLocation(String baseUri, String location)
   {
      baseUri += WEBDAV_CONTEXT;
      if (!location.startsWith(baseUri))
      {
         return null;
      }

      String[] elements = new String[3];
      location = location.substring(baseUri.length());
      elements[0] = location.substring(0, location.indexOf('/'));
      location = location.substring(location.indexOf('/') + 1);
      elements[1] = location.substring(0, location.indexOf('/'));
      elements[2] = location.substring(location.indexOf('/') + 1);
      return elements;
   }

}
