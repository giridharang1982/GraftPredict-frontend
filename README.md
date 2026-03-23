# Backend — Production Setup

This document describes how to run the Flask backend in development and production.

## Quick start (development)

1. Copy `.env.example` to `.env` and fill appropriate values (especially `GOOGLE_CLIENT_ID` and `SECRET_KEY`).
2. Create and activate a Python virtual environment:
   python -m venv .venv
   .\.venv\Scripts\Activate.ps1
3. Install dependencies:
   pip install -r requirements.txt
4. Run the server:
   python complete_backend.py

Note: With `.env` present we load environment variables using `python-dotenv`. In development you may set `DEBUG=true`.

## Production (recommended)

Use a WSGI server such as `gunicorn` and put the service behind a reverse proxy (nginx).

Example (from project root):

    # install deps into a production virtualenv
    pip install -r Backend/requirements.txt

    # run with gunicorn (Linux/Unix)
    gunicorn -w 4 -b 0.0.0.0:5000 Backend.wsgi:app

Windows note (testing / lightweight production):

On Windows, Gunicorn is not supported — use `waitress` for a WSGI server:

    pip install waitress
    waitress-serve --listen=0.0.0.0:5000 Backend.wsgi:app

### Environment variables

Required:
- `GOOGLE_CLIENT_ID` — Google OAuth2 client id for verifying ID tokens
- `SECRET_KEY` — Application secret for signing JWTs

Optional / for features:
- `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SERVER`, `MAIL_PORT`
- `DEBUG` — set to `true` only for development

### Example systemd service (linux)

Create `/etc/systemd/system/graftpredict.service`:

```
[Unit]
Description=GraftPredict backend
After=network.target

[Service]
User=www-data
Group=www-data
WorkingDirectory=/path/to/updated_graft_predict/Backend
EnvironmentFile=/path/to/updated_graft_predict/Backend/.env
ExecStart=/path/to/venv/bin/gunicorn -w 4 -b 127.0.0.1:5000 Backend.wsgi:app
Restart=always

[Install]
WantedBy=multi-user.target
```

Then:

    sudo systemctl daemon-reload
    sudo systemctl enable graftpredict
    sudo systemctl start graftpredict

### Security notes

- Never commit `.env` to source control. Use a secrets manager for production.
- Keep `DEBUG` off in production.
- Ensure your database user/password are not checked in and are secured.
- Consider using HTTPS with a reverse proxy in front of Gunicorn.
