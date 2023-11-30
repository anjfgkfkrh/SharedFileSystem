import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileStructureSaver {
   private ObjectMapper objectMapper = new ObjectMapper();

   public FileNode createFileNode(File file, FileNode parent) {
      FileNode node = new FileNode();
      node.setName(file.getName());
      node.setPath(file.getPath());
      node.SetIsDirectory(false);
      node.setParent(parent);

      if (file.isDirectory()) {
         node.SetIsDirectory(true);
         File[] files = file.listFiles();
         if (files != null) {
            node.setChilds(Arrays.stream(files).map(f -> this.createFileNode(f, node)).collect(Collectors.toList()));
         }
      }
      return node;
   }

   public void saveToFile(FileNode node, Path outputPath) throws IOException {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), node);
   }
}
