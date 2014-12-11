package nl.gridshore.eswp.elasticsearch;

/**
 * Configuration bean for a snapshot repository, contain the mandatory options to create a snapshot repository.
 */
public class SnapshotRepositoryConfig {
    public static final String FILESYSTEM_REPOSITORY = "fs";
    private String name;
    private String type;
    private String location;

    /**
     * Constructor accepting all required parameters to connect to or create a new snapshot repository
     *
     * @param name     String containing the name of the repository
     * @param type     String containing the type of the repository
     * @param location String containing the location of the repository
     */
    public SnapshotRepositoryConfig(String name, String type, String location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    /**
     * Shortcut method to create a filesystem snapshot repository configuration.
     *
     * @param name     String containing the name of the repository
     * @param location String containing the location of the filesystem to write to
     * @return SnapshotRepositoryConfig that is created based on the provided parameters
     */
    public static SnapshotRepositoryConfig buildFileSystem(String name, String location) {
        return new SnapshotRepositoryConfig(name, FILESYSTEM_REPOSITORY, location);
    }

    /**
     * Shortcut method to create a snapshot repository configuration that can only be used with existing repositories
     *
     * @param name String containing the name of the existing repository
     * @return SnapshotRepositoryConfig that is created only for connecting to an existing repository
     */
    public static SnapshotRepositoryConfig buildExisting(String name) {
        return new SnapshotRepositoryConfig(name, null, null);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }
}
