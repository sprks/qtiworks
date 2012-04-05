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
package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * Node's attribute implementation.
 * 
 * @author Jiri Kajaba
 */
public abstract class AbstractAttribute<V> implements Attribute<V> {

    private static final long serialVersionUID = -3172377961902212482L;

    /** Owner of this attribute. */
    private final XmlNode owner;

    /** Name of this attribute. */
    private final String localName;
    
    private final String namespaceUri;
    
    /** Is this attribute mandatory (true) or optional (false). */
    private final boolean required;
    
    /** Attribute value (may be null) */
    protected V value;
    
    /** Attribute default value (may be null) */
    protected V defaultValue;

    public AbstractAttribute(XmlNode owner, String localName, V defaultValue, V value, boolean required) {
        this(owner, localName, "", defaultValue, value, required);
    }

    public AbstractAttribute(XmlNode owner, String localName, String namespaceUri, V defaultValue, V value, boolean required) {
        ConstraintUtilities.ensureNotNull(owner, "owner");
        ConstraintUtilities.ensureNotNull(localName, "localName");
        ConstraintUtilities.ensureNotNull(namespaceUri, "namespaceUri");
        this.owner = owner;
        this.localName = localName;
        this.namespaceUri = namespaceUri;
        this.required = required;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Override
    public final XmlNode getOwner() {
        return owner;
    }

    @Override
    public final String getLocalName() {
        return localName;
    }
    
    @Override
    public final String getNamespaceUri() {
        return namespaceUri;
    }

    @Override
    public final boolean isRequired() {
        return required;
    }
    
    @Override
    public final boolean isSet() {
        return value!=null;
    }
    
    @Override
    public final V getDefaultValue() {
        return defaultValue;
    }
    
    @Override
    public final V getValue() {
        return value;
    }
    
    @Override
    public final void setValue(V value) {
        this.value = value;
    }
    
    @Override
    public final V getComputedValue() {
        return value!=null ? value : defaultValue;
    }

    @Override
    public final String computeXPath() {
        return (owner != null ? owner.computeXPath() + "/" : "") 
                + "@"
                + (!namespaceUri.isEmpty() ? "{" + namespaceUri + "}" : "")
                + localName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(localName=" + localName
                + ",namespaceUri=" + namespaceUri
                + ",required=" + required
                + ",defaultValue=" + defaultValue
                + ",value=" + value
                + ")";
    }
    
    /**
     * Parses value from given QTI string.
     * 
     * @param value string value
     * @return parsed value
     */
    protected abstract V parseQtiString(String value);
    
    /**
     * Subclasses should implement to convert the given value to
     * the String format used in the QTI information model
     */
    protected abstract String toQtiString(V value);

    @Override
    public void validate(ValidationContext context) {
        if (required && value==null) {
            context.add(new AttributeValidationError(this, "Required attribute has not been assigned a value: " + localName));
        }
    }
}
