package uk.nhs.digital.gossmigrator.model.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.enums.ContentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ContentTypeMap extends HashMap<Long, ContentType> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContentTypeMap.class);

    private Set<Long> referencedBy = new HashSet<>();

    @Override
    public ContentType get(Object o) {
        referencedBy.add((Long) o);
        return super.get(o);
    }

    public void logNeverReferenced(){
        for(Long id : this.keySet()){
            if(!referencedBy.contains(id)){
                LOGGER.warn("Id:{}, Type:{}, in DocumentTypeMapping.csv is never used.", id, super.get(id).getDescription());
            }
        }
    }
}
