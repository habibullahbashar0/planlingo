# PlanLingo QGIS Plugin

## Overview
PlanLingo is a QGIS plugin that allows urban planners to perform spatial analyses using natural language queries. Users type queries like:

```
Show all residential parcels within 500m of parks that have experienced flooding in the past 5 years.
```

The plugin sends the query (with project context) to an LLM (via Model Context Protocol or a direct API), receives a structured JSON representation, translates it into a series of PyQGIS processing steps, executes them, and visualizes the results on the map canvas.

## Features
- Dockable panel with text input, Run and Export buttons, and status display.
- Context provider that lists available layers and their attributes.
- AI communication layer supporting MCP server or direct LLM API.
- Query translator that maps JSON to processing functions (buffer, intersect, attribute filter, etc.).
- Wrapper around common QGIS processing algorithms.
- Easy configuration of AI integration method.

## Installation
1. Clone or copy the `planlingo` folder into your QGIS plugins directory:
   - Windows: `%APPDATA%/QGIS/QGIS3/profiles/default/python/plugins/`
2. Ensure the required Python packages are available in QGIS's Python environment (e.g., `requests`).
3. Restart QGIS.
4. Enable **PlanLingo** in the Plugin Manager.

## Configuration
- **AI Integration**: Edit `planlingo/ai_interface.py` to choose `MCPInterface` (default endpoint `http://localhost:5000/mcp`) or `DirectLLMInterface` (provide your API key).
- **Plugin Name**: Adjust `metadata.txt` if you wish to rename the plugin.

## Usage
1. Open the plugin via **Plugins → PlanLingo** or the toolbar button.
2. The dockable panel appears. Type a natural language query.
3. Click **Run**. The plugin will:
   - Gather project context.
   - Send the query to the LLM.
   - Translate the JSON to processing steps.
   - Execute the steps and add the resulting layer to the map.
4. Use **Export Results** (future implementation) to save the output layer.

## Development
- Run tests with `pytest` (tests folder to be added).
- Extend `processing_functions.py` with additional algorithms as needed.
- Improve the prompt engineering in `ai_interface.py` for better JSON generation.

## License
This plugin is released under the GPL‑3.0 license.
