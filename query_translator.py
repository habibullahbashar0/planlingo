import json

class QueryTranslator:
    """Translates structured JSON queries into a sequence of callable processing steps.
    The output is a list of callables (functions) that will be executed by the
    ProcessingFunctions module.
    """

    def __init__(self):
        # Mapping from operation types to processing function names
        self.operation_map = {
            "buffer": "buffer_layer",
            "select": "select_by_attribute",
            "intersect": "intersect_layers",
            "temporal_filter": "filter_by_time",
            # Add more mappings as needed
        }

    def translate(self, structured_query: dict) -> list:
        """Convert a structured query dict into a list of callables.

        The expected format of ``structured_query`` follows the grammar defined
        in the Deepseek.json specification (task 1.2). Example:
        {
            "intent": "show",
            "spatial": {
                "type": "within",
                "distance": 500,
                "reference_layer": "parks"
            },
            "target_layer": "parcels",
            "filters": {
                "attribute": "flooded",
                "value": true,
                "temporal": {
                    "years": 5
                }
            }
        }
        """
        steps = []
        # Example translation logic – this can be expanded based on the full spec.
        # 1. Buffer reference layer if spatial operation is within distance
        spatial = structured_query.get("spatial", {})
        if spatial.get("type") == "within":
            distance = spatial.get("distance")
            ref_layer = spatial.get("reference_layer")
            steps.append({
                "function": self.operation_map["buffer"],
                "args": [ref_layer, distance]
            })
            # The buffer result will be used for intersection
            buffer_result = f"{ref_layer}_buffer"
        else:
            buffer_result = None

        # 2. Intersect with target layer if applicable
        target = structured_query.get("target_layer")
        if buffer_result and target:
            steps.append({
                "function": self.operation_map["intersect"],
                "args": [buffer_result, target]
            })
            current_layer = f"{buffer_result}_x_{target}"
        else:
            current_layer = target

        # 3. Attribute filter
        filters = structured_query.get("filters", {})
        attr = filters.get("attribute")
        value = filters.get("value")
        if attr is not None:
            expression = f"{attr} = {json.dumps(value)}"
            steps.append({
                "function": self.operation_map["select"],
                "args": [current_layer, expression]
            })
            current_layer = f"{current_layer}_selected"

        # 4. Temporal filter
        temporal = filters.get("temporal")
        if temporal:
            years = temporal.get("years")
            steps.append({
                "function": self.operation_map["temporal_filter"],
                "args": [current_layer, years]
            })

        # The final step is to return the name of the resulting layer for the UI to display.
        steps.append({
            "function": "return_layer",
            "args": [current_layer]
        })
        return steps
