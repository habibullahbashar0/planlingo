import json
from qgis.core import QgsProject

class ContextProvider:
    """Provides project context information for the LLM.
    Generates a textual description of available layers and key attributes.
    """

    def __init__(self):
        pass

    def get_context(self) -> str:
        """Return a string summarizing the current QGIS project layers.
        Example output:
        "Available layers: parcels (attributes: id, zoning, flooded), parks (attributes: id, name)."
        """
        layers_info = []
        for layer in QgsProject.instance().mapLayers().values():
            if hasattr(layer, 'fields'):
                fields = [field.name() for field in layer.fields()]
                layers_info.append(f"{layer.name()} (attributes: {', '.join(fields)})")
        if not layers_info:
            return "No layers loaded in the project."
        return "Available layers: " + ", ".join(layers_info) + "."
