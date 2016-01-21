/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource.model.softwaremodule;

import org.eclipse.hawkbit.rest.resource.model.NamedEntityRest;
import org.eclipse.hawkbit.rest.resource.model.doc.ApiModelProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A json annotated rest model for SoftwareModule to RESTful API representation.
 * 
 *
 *
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Software Module")
public class SoftwareModuleRest extends NamedEntityRest {
    /**
     * API definition for {@link SoftwareModule.Type#RUNTIME}.
     */
    public static final String SM_RUNTIME = "runtime";
    /**
     * API definition for {@link SoftwareModule.Type#OS}.
     */
    public static final String SM_OS = "os";
    /**
     * API definition for {@link SoftwareModule.Type#APPLICATION}.
     */
    public static final String SM_APPLICATION = "application";

    @ApiModelProperty(value = ApiModelProperties.ITEM_ID, required = true)
    @JsonProperty(value = "id", required = true)
    private Long moduleId;

    @ApiModelProperty(value = ApiModelProperties.VERSION, required = true)
    @JsonProperty(required = true)
    private String version;

    @ApiModelProperty(value = ApiModelProperties.SOFTWARE_MODULE_TYPE, required = true)
    @JsonProperty(required = true)
    private String type;

    @ApiModelProperty(value = ApiModelProperties.VENDOR)
    @JsonProperty
    private String vendor;

    /**
     * @return the moduleId
     */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId
     *            the moduleId to set
     */
    @JsonIgnore
    public void setModuleId(final Long moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @param vendor
     *            the vendor to set
     */
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

}