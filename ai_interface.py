import abc
import json

class AIInterface(abc.ABC):
    """Abstract base class for AI communication implementations."""

    @abc.abstractmethod
    def get_structured_query(self, user_text: str, context: str) -> dict:
        """Send the user query and context to the LLM and return a structured JSON dict.
        """
        pass

class MCPInterface(AIInterface):
    """Implementation that talks to a Model Context Protocol (MCP) server.
    The server is expected to expose a simple HTTP endpoint that receives a JSON payload
    with `prompt` and returns a JSON string representing the structured query.
    """

    def __init__(self, endpoint_url: str = "http://localhost:5000/mcp"):
        self.endpoint_url = endpoint_url

    def get_structured_query(self, user_text: str, context: str) -> dict:
        import requests
        payload = {
            "prompt": f"Context: {context}\nUser: {user_text}\nReturn a JSON object describing the query.",
            "response_format": "json"
        }
        try:
            resp = requests.post(self.endpoint_url, json=payload, timeout=15)
            resp.raise_for_status()
            # The server should return a JSON string; we parse it safely.
            return json.loads(resp.text)
        except Exception as e:
            # In a real plugin you would log this; here we return an empty dict.
            return {}

class DirectLLMInterface(AIInterface):
    """Implementation that calls a remote LLM API (e.g., OpenAI or Anthropic).
    This is a minimal example; API keys and error handling are omitted for brevity.
    """

    def __init__(self, api_key: str, model: str = "gpt-4o-mini"):
        self.api_key = api_key
        self.model = model
        self.endpoint = "https://api.openai.com/v1/chat/completions"

    def get_structured_query(self, user_text: str, context: str) -> dict:
        import requests
        headers = {"Authorization": f"Bearer {self.api_key}", "Content-Type": "application/json"}
        messages = [
            {"role": "system", "content": "You are a GIS query generator. Return a JSON object only."},
            {"role": "user", "content": f"Context: {context}\nUser query: {user_text}\nProvide a JSON representation of the query."}
        ]
        data = {"model": self.model, "messages": messages, "temperature": 0}
        try:
            resp = requests.post(self.endpoint, headers=headers, json=data, timeout=15)
            resp.raise_for_status()
            result = resp.json()
            # Assume the assistant's reply is in `choices[0].message.content`
            content = result.get("choices", [{}])[0].get("message", {}).get("content", "")
            return json.loads(content)
        except Exception:
            return {}
