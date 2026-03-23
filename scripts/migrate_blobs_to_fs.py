"""
Script to migrate PDF BLOBs from `app_report.pdf_report` to filesystem `uploads/` and update `pdf_path`.
Run with a DB user that has SELECT/UPDATE on `app_report`.
"""
import sys
from db import get_conn
import storage


def migrate(limit=None):
    with get_conn() as conn:
        cur = conn.cursor(dictionary=True)
        cur.execute("SELECT report_id, pdf_report FROM app_report WHERE pdf_report IS NOT NULL")
        rows = cur.fetchall()
        count = 0
        for r in rows:
            rid = r.get('report_id')
            blob = r.get('pdf_report')
            if not blob:
                continue
            path = storage.save_pdf(rid, blob)
            try:
                ucur = conn.cursor()
                ucur.execute("UPDATE app_report SET pdf_path = %s, pdf_report = NULL WHERE report_id = %s", (path, rid))
                conn.commit()
                ucur.close()
                count += 1
            except Exception as e:
                print(f"Failed to update report {rid}: {e}")
        cur.close()
    print(f"Migrated {count} reports")


if __name__ == '__main__':
    migrate()
