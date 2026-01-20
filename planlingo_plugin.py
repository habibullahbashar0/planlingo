import os
from qgis.PyQt import QtWidgets, uic
from qgis.core import QgsProject
from .ai_interface import AIInterface
from .query_translator import QueryTranslator
from .processing_functions import ProcessingFunctions
from .context_provider import ContextProvider

class PlanLingoPlugin:
    """Main class for the PlanLingo QGIS plugin."""

    def __init__(self, iface):
        """Constructor.

        :param iface: An interface instance that will be passed to this class
            which provides the hook to manipulate the QGIS application at run time.
        """
        self.iface = iface
        self.plugin_dir = os.path.dirname(__file__)
        self.dlg = None
        # Choose AI interface implementation (MCP or Direct LLM) – will be set later
        self.ai = None
        self.translator = QueryTranslator()
        self.processor = ProcessingFunctions()
        self.context_provider = ContextProvider()

    def initGui(self):
        """Create the menu entries and toolbar icons inside the QGIS GUI."""
        # Load UI file
        ui_path = os.path.join(self.plugin_dir, "ui_main.ui")
        self.dlg = uic.loadUi(ui_path)
        # Connect signals
        self.dlg.runButton.clicked.connect(self.run_query)
        self.dlg.exportButton.clicked.connect(self.export_results)
        # Add a toolbar button (optional)
        self.action = QtWidgets.QAction("PlanLingo", self.iface.mainWindow())
        self.action.triggered.connect(self.show_dialog)
        self.iface.addToolBarIcon(self.action)
        self.iface.addPluginToMenu("&PlanLingo", self.action)

    def unload(self):
        """Removes the plugin menu item and icon from QGIS GUI."""
        self.iface.removePluginMenu("&PlanLingo", self.action)
        self.iface.removeToolBarIcon(self.action)

    def show_dialog(self):
        """Show the plugin dialog."""
        self.dlg.show()
        self.dlg.raise_()
        self.dlg.activateWindow()

    def run_query(self):
        """Handle the Run button click: send query to AI and execute results."""
        user_text = self.dlg.queryInput.toPlainText().strip()
        if not user_text:
            self.dlg.statusLabel.setText("Please enter a query.")
            return
        self.dlg.statusLabel.setText("Generating query…")
        # Build context string for the LLM
        context = self.context_provider.get_context()
        # Get structured JSON from AI
        if not self.ai:
            self.dlg.statusLabel.setText("AI interface not configured.")
            return
        structured = self.ai.get_structured_query(user_text, context)
        if not structured:
            self.dlg.statusLabel.setText("Failed to obtain structured query.")
            return
        self.dlg.statusLabel.setText("Translating query…")
        callables = self.translator.translate(structured)
        self.dlg.statusLabel.setText("Executing steps…")
        results = self.processor.execute_sequence(callables)
        if results:
            self.dlg.statusLabel.setText("Done. Results added to map.")
        else:
            self.dlg.statusLabel.setText("Execution failed.")

    def export_results(self):
        """Export the last result layer to a file (placeholder)."""
        self.dlg.statusLabel.setText("Export not implemented yet.")
