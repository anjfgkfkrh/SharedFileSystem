import java.io.Serializable;
import java.util.List;

public class FileStructureScan implements Serializable {
    private List<File> files;
    private List<Directory> directories;


    class File implements Serializable{
        private String name;
        private String path;
        private long size;
        private Directory parent;

        public File(String name, String path, long size, Directory parent){
            this.name = name;
            this.path = path;
            this.size = size;
            this.parent = parent;
        }

    }

    class Directory implements Serializable{
        private String name;
        private String path;
        private Directory parent;


    }
}
