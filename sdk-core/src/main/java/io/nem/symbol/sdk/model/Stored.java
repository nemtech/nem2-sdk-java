package io.nem.symbol.sdk.model;

import java.util.Optional;

/**
 * An entity that's stored in the server database.
 *
 * Ideally, clients shouldn't care or use the entities' database Ids. Database Ids can be different between different nodes.
 *
 * Clients should use the entities natural id like account address, mosaic id, namespace id, transaction hash, etc.
 *
 * The database id is currently being used as the default sort by value and it may be used when setting offset values in
 * searches.
 */
public interface Stored {

    /**
     * Returns database id of the entity.
     *
     * @return The database id of the entity if it's known.
     */
    Optional<String> getDatabaseId();

}
