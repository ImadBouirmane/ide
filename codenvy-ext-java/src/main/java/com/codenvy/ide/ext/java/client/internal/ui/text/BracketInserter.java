/*
 * Copyright (C) 2013 eXo Platform SAS.
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
 */
package com.codenvy.ide.ext.java.client.internal.ui.text;

import com.codenvy.ide.ext.java.client.JavaPartitions;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentCommand;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.TypedRegion;
import com.codenvy.ide.texteditor.api.AutoEditStrategy;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BracketInserter implements AutoEditStrategy {


    private TextEditorPartView editor;

    public BracketInserter(TextEditorPartView editor) {
        this.editor = editor;
    }

    private static char getPeerCharacter(char character) {
        switch (character) {
            case '(':
                return ')';

            case ')':
                return '(';

            case '<':
                return '>';

            case '>':
                return '<';

            case '[':
                return ']';

            case ']':
                return '[';

            case '"':
                return character;

            case '\'':
                return character;

            default:
                throw new IllegalArgumentException();
        }
    }

    private boolean isMultilineSelection() {
        return editor.getSelection().hasSelection();
    }

    private boolean isTypeArgumentStart(String identifier) {
        return identifier.length() > 0
               && Character.isUpperCase(identifier.charAt(0));
    }

    private boolean isAngularIntroducer(String identifier) {
        return identifier.length() > 0
               && (Character.isUpperCase(identifier.charAt(0))
                   || identifier.startsWith("final") //$NON-NLS-1$
                   || identifier.startsWith("public") //$NON-NLS-1$
                   || identifier.startsWith("public") //$NON-NLS-1$
                   || identifier.startsWith("protected") //$NON-NLS-1$
                   || identifier.startsWith("private")); //$NON-NLS-1$
    }

    @Override
    public void customizeDocumentCommand(Document document, DocumentCommand command) {
        // early pruning to slow down normal typing as little as possible
        if (!command.doit || isMultilineSelection() || command.text.isEmpty())
            return;

        final char character = command.text.charAt(0);
        switch (character) {
            case '(':
            case '<':
            case '[':
            case '\'':
            case '\"':
                break;
            default:
                return;
        }

        Position selectedRange = editor.getSelection().getSelectedRange();
        final int offset = selectedRange.offset;
        final int length = selectedRange.length;

        try {
            Region startLine = document.getLineInformationOfOffset(offset);
            Region endLine = document.getLineInformationOfOffset(offset + length);

            JavaHeuristicScanner scanner = new JavaHeuristicScanner(document);
            int nextToken = scanner.nextToken(offset + length, endLine.getOffset() + endLine.getLength());
            String next = nextToken == Symbols.TokenEOF ? null : document.get(offset, scanner.getPosition() - offset).trim();
            int prevToken = scanner.previousToken(offset - 1, startLine.getOffset() - 1);
            int prevTokenOffset = scanner.getPosition() + 1;
            String previous = prevToken == Symbols.TokenEOF ? null : document.get(prevTokenOffset, offset - prevTokenOffset).trim();

            switch (character) {
                case '(':
                    if (nextToken == Symbols.TokenLPAREN
                        || nextToken == Symbols.TokenIDENT
                        || next != null && next.length() > 1)
                        return;
                    break;

                case '<':
                    if (
                            nextToken == Symbols.TokenLESSTHAN
                            || nextToken == Symbols.TokenQUESTIONMARK
                            || nextToken == Symbols.TokenIDENT && isTypeArgumentStart(next)
                            || prevToken != Symbols.TokenLBRACE
                               && prevToken != Symbols.TokenRBRACE
                               && prevToken != Symbols.TokenSEMICOLON
                               && prevToken != Symbols.TokenSYNCHRONIZED
                               && prevToken != Symbols.TokenSTATIC
                               && (prevToken != Symbols.TokenIDENT || !isAngularIntroducer(previous))
                               && prevToken != Symbols.TokenEOF)
                        return;
                    break;

                case '[':
                    if (
                            nextToken == Symbols.TokenIDENT
                            || next != null && next.length() > 1)
                        return;
                    break;

                case '\'':
                case '"':
                    if (nextToken == Symbols.TokenIDENT
                        || prevToken == Symbols.TokenIDENT
                        || next != null && next.length() > 1
                        || previous != null && previous.length() > 1)
                        return;
                    break;

                default:
                    return;
            }

            TypedRegion partition = TextUtilities.getPartition(document, JavaPartitions.JAVA_PARTITIONING, offset, true);
            if (!Document.DEFAULT_CONTENT_TYPE.equals(partition.getType()))
                return;

            final char closingCharacter = getPeerCharacter(character);
            final StringBuilder buffer = new StringBuilder();
            buffer.append(character);
            buffer.append(closingCharacter);

//            document.replace(offset, length, buffer.toString());
            command.text = buffer.toString();
            command.caretOffset = offset + 1;
            command.doit = false;

        } catch (BadLocationException e) {
            Log.error(BracketInserter.class, e);
        }
    }

}