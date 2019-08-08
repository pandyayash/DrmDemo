package com.example.drmdemo.model;

import java.io.Serializable;

public class BasicModel implements Serializable {

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDrm_license_url() {
        return drm_license_url;
    }

    public void setDrm_license_url(String drm_license_url) {
        this.drm_license_url = drm_license_url;
    }

    private String drm_license_url;

}
