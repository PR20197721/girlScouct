package org.girlscouts.web.cq.workflow.service.pojo;

import java.util.List;

/**
 * The type Content difference.
 */
public class RolloutContentDifference {
    /**
     * The Old content.
     */
    private String oldContent;
    /**
     * The New content.
     */
    private String newContent;
    /**
     * The Component resource type.
     */
    private String componentResourceType;
    /**
     * The Property name.
     */
    private String propertyName;
    /**
     * The Is inheritance broken.
     */
    private Boolean isInheritanceBroken;

    /**
     * Instantiates a new Content difference.
     *
     * @param oldContent            the old content
     * @param newContent            the new content
     * @param componentResourceType the component resource type
     * @param propertyName          the property name
     */
    public RolloutContentDifference(String oldContent, String newContent, String componentResourceType, String propertyName) {
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.componentResourceType = componentResourceType;
        this.propertyName = propertyName;
    }

    /**
     * Instantiates a new Content difference.
     *
     * @param oldContent            the old content
     * @param newContent            the new content
     * @param componentResourceType the component resource type
     * @param propertyName          the property name
     * @param isInheritanceBroken   the isInheritanceBroken
     */
    public RolloutContentDifference(String oldContent, String newContent, String componentResourceType, String propertyName,Boolean isInheritanceBroken) {
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.componentResourceType = componentResourceType;
        this.propertyName = propertyName;
        this.isInheritanceBroken = isInheritanceBroken;
    }

    /**
     * Gets old content.
     *
     * @return the old content
     */
    public String getOldContent() {
        return oldContent;
    }

    /**
     * Gets new content.
     *
     * @return the new content
     */
    public String getNewContent() {
        return newContent;
    }

    /**
     * Gets component resource type.
     *
     * @return the component resource type
     */
    public String getComponentResourceType() {
        return componentResourceType;
    }

    /**
     * Gets property name.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Is inheritance broken boolean.
     *
     * @return the boolean
     */
    public Boolean isInheritanceBroken() {
        return isInheritanceBroken;
    }

    /**
     * Sets inheritance broken.
     *
     * @param inheritanceBroken the inheritance broken
     */
    public void setInheritanceBroken(Boolean inheritanceBroken) {
        isInheritanceBroken = inheritanceBroken;
    }
}
