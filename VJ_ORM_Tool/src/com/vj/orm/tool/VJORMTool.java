package com.vj.orm.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.UnitValue;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.jar.*;
import javax.tools.*;

public class VJORMTool {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java VJORMTool <config.json>");
            System.exit(1);
        }

        try {
            VJORMTool tool = new VJORMTool();
            tool.execute(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void execute(String configPath) throws Exception {
        // 1. Read Config
        String content = new String(Files.readAllBytes(Paths.get(configPath)));
        JsonObject config = new Gson().fromJson(content, JsonObject.class);
        
        JsonObject jdbc = config.getAsJsonObject("jdbc");
        JsonObject gen = config.getAsJsonObject("generator");

        String dbUrl = jdbc.get("url").getAsString();
        String user = jdbc.get("username").getAsString();
        String pass = jdbc.get("password").getAsString();
        String pkg = gen.get("packageName").getAsString();
        String outputJarName = gen.get("outputJar").getAsString();
        boolean enableCache = gen.has("enableCache") && gen.get("enableCache").getAsBoolean();

        System.out.println("Connecting to Database: " + dbUrl);
        
        // 2. Scan Schema
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Create Folder Structure
            Path root = Paths.get("generated_project");
            Path srcPath = root.resolve("src").resolve(pkg.replace(".", "/"));
            Path distPath = root.resolve("dist");
            Path configPathDir = root.resolve("config");
            
            Files.createDirectories(srcPath);
            Files.createDirectories(distPath);
            Files.createDirectories(configPathDir);

            // Fetch Tables & Views
            List<String> types = new ArrayList<>();
            types.add("TABLE");
            if (gen.has("scanViews") && gen.get("scanViews").getAsBoolean()) types.add("VIEW");
            
            ResultSet rs = metaData.getTables(null, null, "%", types.toArray(new String[0]));
            List<TableMetadata> tables = new ArrayList<>();
            
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String tableType = rs.getString("TABLE_TYPE");
                tables.add(extractMetadata(metaData, tableName, tableType));
            }

            // 3. Generate POJOs
            List<File> javaFiles = new ArrayList<>();
            for (TableMetadata table : tables) {
                File file = generateJavaFile(srcPath, pkg, table, enableCache);
                javaFiles.add(file);
                System.out.println("Generated: " + file.getName());
            }

            // 4. Create conf.json for runtime
            generateRuntimeConfig(configPathDir, jdbc);

            // 5. Compile and Package
            System.out.println("Compiling Generated Code...");
            compileAndJar(root, javaFiles, pkg, outputJarName);

            // 6. Generate PDF Documentation
            System.out.println("Generating Documentation...");
            generateDocumentation(distPath.resolve("pojo.pdf").toString(), tables, pkg);

            System.out.println("\nSuccessfully created project in: " + root.toAbsolutePath());
            System.out.println("Final JAR: " + distPath.resolve(outputJarName));
        }
    }

    private TableMetadata extractMetadata(DatabaseMetaData meta, String tableName, String type) throws SQLException {
        TableMetadata metadata = new TableMetadata(tableName, type);
        
        // Columns
        try (ResultSet rs = meta.getColumns(null, null, tableName, "%")) {
            while (rs.next()) {
                metadata.addColumn(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
            }
        }
        
        // Primary Keys
        try (ResultSet rs = meta.getPrimaryKeys(null, null, tableName)) {
            while (rs.next()) {
                metadata.setPrimaryKey(rs.getString("COLUMN_NAME"));
            }
        }
        
        // Auto Increment (Guessing based on first column if PK and numeric, or use metadata)
        // Simplification for now: we'll check if table has an auto-inc column
        // (MySQL specific metadata could be used here)
        
        return metadata;
    }

    private File generateJavaFile(Path outDir, String pkg, TableMetadata table, boolean enableCache) throws IOException {
        String className = toCamelCase(table.name, true);
        Path filePath = outDir.resolve(className + ".java");
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println("package " + pkg + ";");
            writer.println();
            writer.println("import com.vj.orm.annotation.*;");
            writer.println();
            writer.println("@Table(name=\"" + table.name + "\")");
            if (enableCache && table.type.equals("TABLE")) {
                writer.println("@Cacheable");
            }
            writer.println("public class " + className + " {");
            
            for (Column col : table.columns) {
                writer.println();
                if (col.isPrimaryKey) {
                    writer.println("    @PrimaryKey");
                    // Simple heuristic for auto-increment (usually PK)
                    // In a real tool, we'd check JDBC IS_AUTOINCREMENT
                    writer.println("    @AutoIncrement");
                }
                writer.println("    @Column(name=\"" + col.name + "\")");
                writer.println("    public " + mapType(col.type) + " " + toCamelCase(col.name, false) + ";");
            }
            
            writer.println("}");
        }
        return filePath.toFile();
    }

    private void compileAndJar(Path root, List<File> files, String pkg, String jarName) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Path binPath = root.resolve("classes");
        Files.createDirectories(binPath);

        List<String> optionList = new ArrayList<>();
        optionList.add("-d");
        optionList.add(binPath.toString());
        optionList.add("-classpath");
        optionList.add("lib/vj-orm-core.jar;lib/gson-2.13.1.jar"); // Core dependency

        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(files);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

        if (!task.call()) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.err.println(diagnostic.getMessage(null));
            }
            throw new Exception("Compilation failed!");
        }

        // JAR it
        Path jarFile = root.resolve("dist").resolve(jarName);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFile.toFile()), manifest)) {
            addDirToJar(binPath, binPath, target);
        }
    }

    private void addDirToJar(Path root, Path source, JarOutputStream target) throws IOException {
        Files.walk(source).filter(Files::isRegularFile).forEach(path -> {
            try {
                String name = root.relativize(path).toString().replace("\\", "/");
                JarEntry entry = new JarEntry(name);
                target.putNextEntry(entry);
                Files.copy(path, target);
                target.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateDocumentation(String dest, List<TableMetadata> tables, String pkg) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph("VJ ORM Framework Documentation").setFontSize(18).setBold());
        document.add(new Paragraph("Package: " + pkg));
        document.add(new Paragraph("Generated at: " + new java.util.Date()));
        
        for (TableMetadata table : tables) {
            document.add(new Paragraph("\nClass: " + toCamelCase(table.name, true)).setBold());
            document.add(new Paragraph("Table Name: " + table.name + " (" + table.type + ")"));
            
            Table pdfTable = new Table(UnitValue.createPercentArray(new float[]{30, 40, 30})).useAllAvailableWidth();
            pdfTable.addHeaderCell("Field Name");
            pdfTable.addHeaderCell("Column name");
            pdfTable.addHeaderCell("Type");
            
            for (Column col : table.columns) {
                pdfTable.addCell(toCamelCase(col.name, false));
                pdfTable.addCell(col.name + (col.isPrimaryKey ? " (PK)" : ""));
                pdfTable.addCell(mapType(col.type));
            }
            document.add(pdfTable);
        }
        
        document.close();
    }

    private void generateRuntimeConfig(Path outDir, JsonObject jdbc) throws IOException {
        Path path = outDir.resolve("conf.json");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println(new Gson().toJson(jdbc));
        }
    }

    private String toCamelCase(String s, boolean firstUpper) {
        String[] parts = s.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() == 0) continue;
            if (sb.length() == 0 && !firstUpper) {
                sb.append(part.toLowerCase());
            } else {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private String mapType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.contains("int")) return "int";
        if (sqlType.contains("char") || sqlType.contains("text")) return "String";
        if (sqlType.contains("date") || sqlType.contains("time")) return "java.sql.Date";
        return "Object";
    }

    private static class TableMetadata {
        String name;
        String type;
        List<Column> columns = new ArrayList<>();
        
        TableMetadata(String name, String type) {
            this.name = name;
            this.type = type;
        }
        
        void addColumn(String name, String type) {
            columns.add(new Column(name, type));
        }
        
        void setPrimaryKey(String colName) {
            for (Column c : columns) {
                if (c.name.equals(colName)) c.isPrimaryKey = true;
            }
        }
    }

    private static class Column {
        String name;
        String type;
        boolean isPrimaryKey;
        Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
