.PHONY: run test lint format

run:
	uvicorn backend.app.main:app --reload

test:
	pytest -q

lint:
	ruff .

format:
	black .
