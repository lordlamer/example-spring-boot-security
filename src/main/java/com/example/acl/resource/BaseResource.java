package com.example.acl.resource;

/**
 * Created by fhabermann on 01.03.2017.
 */
public class BaseResource implements ResourceInterface {
    private String resourceId;

    public BaseResource() {

    }

    public BaseResource(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
