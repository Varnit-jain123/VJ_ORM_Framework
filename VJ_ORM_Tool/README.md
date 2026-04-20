# VJ ORM Automation Tool

This tool is a dedicated utility for the **VJ ORM Framework**. It automatically generates Java Entity classes (POJOs), compiles them, and packages them into a JAR based on your existing database schema.

---

##  Folder Structure

```text
VJ_ORM_Tool/
├── src/
│   └── com/vj/orm/tool/    (Tool Source Code)
├── lib/                    (Tool Dependencies)
├── classes/                (Compiled Bytecode)
├── generated_project/      (Automation Output)
│   ├── src/                (Generated POJOs)
│   ├── dist/               (Final JAR & PDF)
│   └── config/             (Runtime Config)
├── build_tool.bat
├── run_tool.bat
├── tool_config.json
└── README.md
```

- **`src/`**: Source code for the `VJORMTool`.
- **`lib/`**: All dependencies needed for generation (iText 7, Gson, MySQL).
- **`generated_project/`**: This directory is created when you run the tool. It contains the output (JAR and PDF).
- **`classes/`**: Compiled classes of the tool itself.

---

##  Configuration (`tool_config.json`)
Before running, update `tool_config.json` with your database details:

```json
{
  "jdbc": {
    "url": "jdbc:mysql://localhost:3306/your_db",
    "username": "...",
    "password": "..."
  },
  "generator": {
    "packageName": "com.myproject.entities",
    "outputJar": "my-entities.jar",
    "enableCache": true,
    "scanViews": true
  }
}
```

---

##  How to Run

1.  **Build the Tool**: 
    If you change the source, run:
    ```bash
    build_tool.bat
    ```

2.  **Generate your Entities**:
    Execute the automation pipeline:
    ```bash
    run_tool.bat
    ```

---

##  Output
Once finished, check the **`generated_project/dist/`** folder for:
- **`your-entities.jar`**: The final JAR to include in your Java project.
- **`pojo.pdf`**: A documentation report of all mapped tables and columns.
