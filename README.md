# VJ ORM Framework

**VJ ORM** is a lightweight, annotation-driven Object-Relational Mapping (ORM) framework for Java. It is designed to empower developers (like Bobby!) to interact with databases without writing complex SQL, manual POJO classes, or tedious boilerplate mapping code.

---

##  Key Features

- **Fluent Query API**: Fetch data with intuitive, chainable methods like `.where("age").gt(20).list()`.
- **Intelligent Caching**: Use the `@Cacheable` annotation to store frequently accessed data in memory, with automatic deep-cloning for safety.
- **Auto-CRUD Operations**: Simple `save()`, `update()`, and `delete()` methods that use reflection to handle everything.
- **Database View Support**: Seamlessly read from MySQL Views with built-in read-only protection.
- **Tooling & Automation**: A professional generator tool that introspects your DB and creates ready-to-use JARs and PDF documentation.

---

##  Project Structure

```text
VJ_orm_rock/
├── src/
│   └── com/vj/orm/
│       ├── annotation/     (ORM Annotations)
│       ├── config/         (Configuration Logic)
│       ├── core/           (ORM Engine)
│       └── exception/      (Error Handling)
├── lib/
│   ├── itext7/             (PDF Libraries)
│   ├── gson-2.13.1.jar
│   ├── mysql.jar
│   └── vj-orm-core.jar     (Bundled Framework)
├── testcases/              (Feature Demos)
├── classes/                (Compiled Bytecode)
├── VJ_ORM_Tool/            (Automation Toolset)
├── conf.json               (DB Configuration)
└── README.md
```

The project is organized into two primary components: the core framework engine and the automation toolset.

### Core Framework Packages
- **com.vj.orm.annotation**: Contains mapping annotations (`@Table`, `@Column`, `@Cacheable`, etc.).
- **com.vj.orm.core**: The heart of the ORM, including the `DataManager` and fluent `QueryBuilder`.
- **com.vj.orm.config**: Utilities for reading and managing database connection settings.
- **com.vj.orm.exception**: Centralized exception handling via `DataException`.

### Automation & Tooling
- **VJ_ORM_Tool**: A standalone environment for scanning databases and generating entities.
- **lib**: Central repository for framework dependencies (Gson, iText 7, MySQL Connector).
- **testcases**: A collection of examples and verification scripts for each framework phase.

---

##  Development Workflow

Using the VJ ORM framework follows a structured lifecycle designed for maximum developer efficiency:

1.  **Database Preparation**: Define your schema, tables, and views in MySQL.
2.  **Entity Automation**: Configure the `VJ_ORM_Tool` and run it to generate annotated Java POJOs and a metadata PDF.
3.  **Framework Bundling**: Pack the core framework into `vj-orm-core.jar` for use as a dependency.
4.  **Application Integration**: Include the core JAR and the generated entity JAR in your project's classpath.
5.  **Initialization**: Call `DataManager.init()` at startup to pre-load caches for optimized performance.
6.  **Persistence Logic**: Use the `DataManager` and `QueryBuilder` APIs for all database interactions.

---

##  Usage Guide

### 1. Project Configuration
Ensure you have a `conf.json` in your project root with your database credentials:
```json
{
  "jdbc-driver": "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/your_db",
  "username": "your_user",
  "password": "your_password"
}
```

### 2. Initializing the Framework
The framework must be initialized at startup, especially if you use caching:
```java
// Initialize the global cache for @Cacheable entities
DataManager.init();

// Standard transaction logic
DataManager dm = DataManager.getDataManager();
dm.begin();
try {
    // ... your ORM operations ...
    dm.end(); // Commits transaction
} catch (DataException de) {
    System.err.println("Error: " + de.getMessage());
}
```

### 3. Basic CRUD Operations
Save, update, or delete objects using reflection:
```java
Course c = new Course();
c.title = "Advanced Java";
int generatedId = dm.save(c);

c.title = "Mastering Java";
dm.update(c);

dm.delete(Course.class, generatedId);
```

### 4. Advanced Filtering (Fluent API)
Query your data without writing SQL:
```java
List<Student> students = dm.query(Student.class)
    .where("rollNumber").gt(100)
    .or("firstName").eq("Bobby")
    .list();
```

---

##  Detailed Step-by-Step Execution Guide

Follow these steps to set up and run a project from scratch:

### Step 1: Bundle the Core Framework
Before the tool can generate entities, the core framework must be bundled into a JAR.
1.  Open your terminal in the project root.
2.  Run the following command:
    ```bash
    jar cvf lib/vj-orm-core.jar -C classes com/vj/orm/annotation -C classes com/vj/orm/config -C classes com/vj/orm/core -C classes com/vj/orm/exception
    ```
    *This creates `vj-orm-core.jar` in your `lib/` folder.*

### Step 2: Generate your Entities (POJOs)
Use the automated tool to scan your database and create your project structure.
1.  Navigate to the **`VJ_ORM_Tool/`** folder.
2.  Configure your database details in `tool_config.json`.
3.  Run the generation pipeline:
    ```bash
    run_tool.bat
    ```
    *This will create the `generated_project/` folder containing `bobby-orm.jar` and `pojo.pdf`.*

### Step 3: Run your Application
Once you have the generated JARs, you can use them in your own Java application.

**1. Copy the JARs**:
Ensure `vj-orm-core.jar` (the framework) and `bobby-orm.jar` (your entities) are in your classpath.

**2. Writing your code**:
```java
import com.vj.orm.core.*;
import bobby_project.*; // The package name you chose in tool_config.json

public class MyApp {
    public static void main(String[] args) throws Exception {
        DataManager.init(); // Load cache
        DataManager dm = DataManager.getDataManager();
        dm.begin();
        
        List<Student> students = dm.query(Student.class).list();
        for(Student s : students) System.out.println(s.firstName);
        
        dm.end();
    }
}
```

**3. Compile and Run**:
```bash
# Compile
javac -cp "lib/vj-orm-core.jar;VJ_ORM_Tool/generated_project/dist/bobby-orm.jar;lib/gson-2.13.1.jar;lib/mysql.jar" MyApp.java

# Run
java -cp ".;lib/vj-orm-core.jar;VJ_ORM_Tool/generated_project/dist/bobby-orm.jar;lib/gson-2.13.1.jar;lib/mysql.jar" MyApp
```

---

##  The VJ ORM Tool (Automation)
Located in the `VJ_ORM_Tool/` folder, this utility automates the entire setup process.

### How to use the Tool:
1.  Configure `tool_config.json` with your database credentials.
2.  Run `run_tool.bat`.
3.  **Result**: Find your compiled entities in `dist/bobby-orm.jar` and your schema report in `dist/pojo.pdf`.

---

##  Requirements
- **JDK**: Java 8 or higher.
- **Database**: MySQL 8.0+.
- **Libraries**: Included in the `lib/` folder (Gson, iText 7, MySQL Connector).

---

##  Running Framework Test Cases

We have included a comprehensive suite of test cases to verify each feature of the framework. These are located in the `testcases/` folder.

### Quick Start:
To run any test phase, simply execute the corresponding batch script from the project root:

| Phase | Feature | Script |
| :--- | :--- | :--- |
| **Phase 1** | Metadata Extraction | `run_phase1.bat` |
| **Phase 3** | INSERT Operation | `run_phase3.bat` |
| **Phase 4** | UPDATE/DELETE | `run_phase4.bat` |
| **Phase 7** | SELECT (Fetch All) | `run_phase7.bat` |
| **Phase 8** | Fluent Query Filtering | `run_phase8.bat` |
| **Phase 9** | Database View Support | `run_phase9.bat` |
| **Phase 10** | In-memory Caching | `run_phase10.bat` |

### Manual Execution:
If you prefer to run them manually from the command line:
```bash
java -cp "classes;lib/*" testcases.eg10psp
```

---


*Built By Varnit Jain*
