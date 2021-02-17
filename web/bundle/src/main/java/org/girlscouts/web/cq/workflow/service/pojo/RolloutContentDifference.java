package org.girlscouts.web.cq.workflow.service.pojo;

/**
 * The type Content difference.
 */
public class RolloutContentDifference {
    private String oldContent;
    private String newContent;
    private String componentResourceType;
    private String propertyName;

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
}
