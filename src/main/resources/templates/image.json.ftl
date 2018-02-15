<#-- @ftlvariable name="image" type="uk.nhs.digital.gossmigrator.model.hippo.Image" -->
{
  "name" : "${image.jcrNodeName}",
  "primaryType" : "hippogallery:imageset",
  "mixinTypes" : [ ],
  "properties" : [ {
    "name" : "hippogallery:filename",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${image.localizedName}" ]
  }, {
    "name" : "jcr:path",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${image.jcrPath}" ]
  }, {
    "name" : "jcr:localizedName",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${image.localizedName}" ]
  } ],
  "nodes" : [ {
    "name" : "hippogallery:thumbnail",
    "primaryType" : "hippogallery:image",
    "mixinTypes" : [ ],
    "properties" : [ {
      "name" : "hippogallery:height",
      "type" : "LONG",
      "multiple" : false,
      "values" : [ "51" ]
    }, {
      "name" : "hippogallery:width",
      "type" : "LONG",
      "multiple" : false,
      "values" : [ "60" ]
    }, {
      "name" : "jcr:data",
      "type" : "BINARY",
      "multiple" : false,
      "values" : [ "${image.filePath}" ]
    }, {
      "name" : "jcr:lastModified",
      "type" : "DATE",
      "multiple" : false,
      "values" : [ "2018-02-12T13:04:39.526Z" ]
    }, {
      "name" : "jcr:mimeType",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${image.mimeType}" ]
    }, {
      "name" : "hippo:filename",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${image.localizedName}" ]
    } ],
    "nodes" : [ ]
  }, {
    "name" : "hippogallery:original",
    "primaryType" : "hippogallery:image",
    "mixinTypes" : [ ],
    "properties" : [ {
      "name" : "hippogallery:height",
      "type" : "LONG",
      "multiple" : false,
      "values" : [ "${image.height}" ]
    }, {
      "name" : "hippogallery:width",
      "type" : "LONG",
      "multiple" : false,
      "values" : [ "${image.width}" ]
    }, {
      "name" : "jcr:data",
      "type" : "BINARY",
      "multiple" : false,
      "values" : [ "${image.filePath}" ]
    }, {
      "name" : "jcr:lastModified",
      "type" : "DATE",
      "multiple" : false,
      "values" : [ "2018-02-12T13:04:39.526Z" ]
    }, {
      "name" : "jcr:mimeType",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${image.mimeType}" ]
    } ],
    "nodes" : [ ]
  } ]
}