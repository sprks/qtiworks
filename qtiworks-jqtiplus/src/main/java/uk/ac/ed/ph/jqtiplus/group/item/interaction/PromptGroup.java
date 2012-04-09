/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.group.item.interaction;

import uk.ac.ed.ph.jqtiplus.group.AbstractNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.BlockInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;

/**
 * Group of prompt child.
 *
 * @author Jonathon Hare
 */
public final class PromptGroup extends AbstractNodeGroup<Prompt> {

    private static final long serialVersionUID = -3440936238732152194L;

    /**
     * Constructs group.
     *
     * @param parent parent of created group
     */
    public PromptGroup(BlockInteraction parent) {
        super(parent, Prompt.QTI_CLASS_NAME, false);
    }

    /**
     * Gets prompt child.
     *
     * @return Prompt child, or null if it doesn't exist.
     * @see #setPrompt
     */
    public Prompt getPrompt() {
        return getChild();
    }

    /**
     * Sets prompt child.
     *
     * @param prompt Prompt child to set.
     * @see #getPrompt
     */
    public void setPrompt(Prompt prompt) {
        setChild(prompt);
    }

    /**
     * Creates child with given QTI class name.
     * <p>
     * Parameter classTag is needed only if group can contain children with different QTI class names.
     *
     * @param classTag QTI class name (this parameter is ignored)
     * @return created child
     */
    @Override
    public Prompt create(String classTag) {
        return new Prompt(getParent());
    }
}
