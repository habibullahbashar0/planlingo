import importlib
from qgis.core import QgsProject, QgsVectorLayer, QgsProcessingFeedback

class ProcessingFunctions:
    """Wrapper around PyQGIS processing algorithms.
    Provides methods that correspond to the function names used by QueryTranslator.
    """

    def __init__(self):
        self.feedback = QgsProcessingFeedback()

    def buffer_layer(self, layer_name: str, distance: float):
        """Create a buffer around the given layer.
        Returns the name of the new buffered layer.
        """
        layer = self._get_layer_by_name(layer_name)
        if not layer:
            return None
        params = {
            'INPUT': layer,
            'DISTANCE': distance,
            'SEGMENTS': 10,
            'END_CAP_STYLE': 0,
            'JOIN_STYLE': 0,
            'MITER_LIMIT': 2,
            'DISSOLVE': False,
            'OUTPUT': 'memory:'
        }
        result = processing.run('native:buffer', params, feedback=self.feedback)
        buffered = result['OUTPUT']
        name = f"{layer_name}_buffer"
        QgsProject.instance().addMapLayer(buffered, False)
        buffered.setName(name)
        return name

    def select_by_attribute(self, layer_name: str, expression: str):
        """Select features in a layer by an attribute expression.
        Returns the same layer name after selection.
        """
        layer = self._get_layer_by_name(layer_name)
        if not layer:
            return None
        layer.selectByExpression(expression, QgsVectorLayer.SetSelection)
        return layer_name

    def intersect_layers(self, layer_a_name: str, layer_b_name: str):
        """Intersect two vector layers.
        Returns the name of the resulting intersection layer.
        """
        layer_a = self._get_layer_by_name(layer_a_name)
        layer_b = self._get_layer_by_name(layer_b_name)
        if not layer_a or not layer_b:
            return None
        params = {
            'INPUT': layer_a,
            'OVERLAY': layer_b,
            'OUTPUT': 'memory:'
        }
        result = processing.run('native:intersection', params, feedback=self.feedback)
        intersected = result['OUTPUT']
        name = f"{layer_a_name}_x_{layer_b_name}"
        QgsProject.instance().addMapLayer(intersected, False)
        intersected.setName(name)
        return name

    def filter_by_time(self, layer_name: str, years: int):
        """Placeholder for temporal filtering.
        In a real implementation this would filter based on a date attribute.
        """
        # For now just return the same layer name.
        return layer_name

    def execute_sequence(self, steps: list):
        """Execute a list of step dictionaries produced by QueryTranslator.
        Each step dict has keys 'function' and 'args'.
        """
        last_result = None
        for step in steps:
            func_name = step['function']
            args = step.get('args', [])
            if func_name == 'return_layer':
                # Final step: return the layer name to the UI.
                return args[0]
            # Resolve method on this class
            method = getattr(self, func_name, None)
            if not method:
                # Unknown function, skip
                continue
            last_result = method(*args)
        return last_result

    def _get_layer_by_name(self, name: str):
        """Helper to retrieve a layer from the current project by its name."""
        for layer in QgsProject.instance().mapLayers().values():
            if layer.name() == name:
                return layer
        return None
