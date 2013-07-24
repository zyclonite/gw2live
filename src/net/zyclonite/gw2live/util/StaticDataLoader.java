/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.zyclonite.gw2live.model.Coordinate;
import net.zyclonite.gw2live.model.KeyValueLanguage;
import net.zyclonite.gw2live.model.WvwObjectiveDetails;
import net.zyclonite.gw2live.service.MongoDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author zyclonite
 */
public class StaticDataLoader {

    private static final Log LOG = LogFactory.getLog(StaticDataLoader.class);
    private final String directory;
    private final MongoDB db;
    private final ObjectMapper mapper;

    public StaticDataLoader() {
        mapper = new ObjectMapper();
        db = MongoDB.getInstance();
        final AppConfig config = AppConfig.getInstance();
        directory = config.getString("application.staticdata", "./import");
    }

    public void loadData() {
        doWvwObjectiveDetails();
        doWvwObjectiveLongNames();
        doWvwMapNames();
        doWvwCoordinates();
        doPveCoordinates();
    }
    
    private void doWvwObjectiveDetails() {
        try {
            final File file = new File(directory + File.separator + "objectivedetails.json");
            if (file.exists()) {
                final List<WvwObjectiveDetails> objectivedetails = mapper.readValue(file, new TypeReference<List<WvwObjectiveDetails>>() {
                });
                db.saveWvwObjectiveDetails(objectivedetails);
            }
        } catch (JsonParseException | JsonMappingException e) {
            LOG.error("Could not parse objectivedetails.json " + e);
        } catch (IOException ex) {
            LOG.error("Coudl load objectivedetails.json " + ex);
        }
        LOG.debug("objectivedetails.json loaded");
    }

    private void doWvwObjectiveLongNames() {
        try {
            final File file = new File(directory + File.separator + "objectivelongnames.json");
            if (file.exists()) {
                final List<KeyValueLanguage> objectivelongnames = mapper.readValue(file, new TypeReference<List<KeyValueLanguage>>() {
                });
                db.saveWvwObjectiveLongNames(objectivelongnames);
            }
        } catch (JsonParseException | JsonMappingException e) {
            LOG.error("Could not parse objectivelongnames.json " + e);
        } catch (IOException ex) {
            LOG.error("Coudl load objectivelongnames.json " + ex);
        }
        LOG.debug("objectivelongnames.json loaded");
    }

    private void doWvwMapNames() {
        try {
            final File file = new File(directory + File.separator + "wvwmapnames.json");
            if (file.exists()) {
                final List<KeyValueLanguage> wvwmapnames = mapper.readValue(file, new TypeReference<List<KeyValueLanguage>>() {
                });
                db.saveWvwMapNames(wvwmapnames);
            }
        } catch (JsonParseException | JsonMappingException e) {
            LOG.error("Could not parse wvwmapnames.json " + e);
        } catch (IOException ex) {
            LOG.error("Coudl load wvwmapnames.json " + ex);
        }
        LOG.debug("wvwmapnames.json loaded");
    }

    private void doWvwCoordinates() {
        try {
            final File file = new File(directory + File.separator + "wvwcoordinates.json");
            if (file.exists()) {
                final List<Coordinate> wvwcoordinates = mapper.readValue(file, new TypeReference<List<Coordinate>>() {
                });
                db.saveWvwCoordinates(wvwcoordinates);
            }
        } catch (JsonParseException | JsonMappingException e) {
            LOG.error("Could not parse wvwcoordinates.json " + e);
        } catch (IOException ex) {
            LOG.error("Coudl load wvwcoordinates.json " + ex);
        }
        LOG.debug("wvwcoordinates.json loaded");
    }

    private void doPveCoordinates() {
        try {
            final File file = new File(directory + File.separator + "pvecoordinates.json");
            if (file.exists()) {
                final List<Coordinate> pvecoordinates = mapper.readValue(file, new TypeReference<List<Coordinate>>() {
                });
                db.savePveCoordinates(pvecoordinates);
            }
        } catch (JsonParseException | JsonMappingException e) {
            LOG.error("Could not parse pvecoordinates.json " + e);
        } catch (IOException ex) {
            LOG.error("Coudl load pvecoordinates.json " + ex);
        }
        LOG.debug("pvecoordinates.json loaded");
    }
}
