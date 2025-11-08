#!/usr/bin/env python3
"""
Filter a JaCoCo XML report to produce a compact HTML listing only classes that have a test file in the repo.
Usage:
  python3 tools/filter_jacoco.py --jacoco target/site/jacoco/jacoco.xml --srcdir project --out target/site/jacoco/filtered_index.html
"""
import argparse
import os
import xml.etree.ElementTree as ET


def find_test_targets(srcdir):
    """Return set of source filenames (e.g. 'Receipt.java') that have a matching *Test.java in srcdir."""
    targets = set()
    for root, _, files in os.walk(srcdir):
        for f in files:
            if f.endswith("Test.java"):
                base = f[:-len("Test.java")]
                targets.add(base + ".java")
    return targets


def parse_jacoco(jacoco_xml_path):
    tree = ET.parse(jacoco_xml_path)
    root = tree.getroot()
    ns = {}
    classes = []
    for pkg in root.findall('package'):
        for cls in pkg.findall('class'):
            name = cls.get('name')
            src = cls.get('sourcefilename')
            # find line counter
            missed = covered = None
            for c in cls.findall('counter'):
                if c.get('type') == 'LINE':
                    missed = int(c.get('missed'))
                    covered = int(c.get('covered'))
                    break
            classes.append({'name': name, 'source': src, 'missed': missed, 'covered': covered})
    return classes


def render_html(classes, out_path):
    total_covered = sum(c['covered'] for c in classes)
    total_lines = sum((c['covered'] + c['missed']) for c in classes)
    pct = (100.0 * total_covered / total_lines) if total_lines else 0.0

    rows = []
    for c in classes:
        lines = c['covered'] + (c['missed'] or 0)
        percent = (100.0 * c['covered'] / lines) if lines else 0.0
        rows.append((c['name'], c['source'], c['covered'], c['missed'], percent))

    rows.sort(key=lambda r: (-r[4], r[0]))

    html = ["<html><head><meta charset=\"utf-8\"><title>Filtered JaCoCo report</title>",
            "<style>body{font-family:Arial,Helvetica,sans-serif;margin:20px}table{border-collapse:collapse;width:900px}th,td{border:1px solid #ccc;padding:8px;text-align:left}th{background:#f0f0f0}</style>",
            "</head><body>"]
    html.append(f"<h2>Filtered coverage report ({len(rows)} classes) — combined {pct:.1f}%</h2>")
    html.append("<table>")
    html.append("<tr><th>Class (internal)</th><th>Source file</th><th>Covered</th><th>Missed</th><th>Line %</th></tr>")
    for name, src, covered, missed, percent in rows:
        html.append(f"<tr><td>{name}</td><td>{src}</td><td>{covered}</td><td>{missed}</td><td>{percent:.1f}%</td></tr>")
    html.append("</table>")
    html.append("<p>Note: this is a compact filtered view showing only classes that have a matching *Test.java in the source tree.</p>")
    html.append("</body></html>")

    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(html))
    print(f"Wrote filtered report to {out_path}")


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--jacoco', default='target/site/jacoco/jacoco.xml')
    p.add_argument('--srcdir', default='project')
    p.add_argument('--out', default='target/site/jacoco/filtered_index.html')
    args = p.parse_args()

    if not os.path.exists(args.jacoco):
        print('Jacoco XML not found:', args.jacoco)
        return 2

    tst_targets = find_test_targets(args.srcdir)
    if not tst_targets:
        print('No *Test.java files found under', args.srcdir)
        return 2

    classes = parse_jacoco(args.jacoco)
    filtered = [c for c in classes if c['source'] in tst_targets]

    if not filtered:
        print('No matching classes found in jacoco report for test targets')
        return 2

    render_html(filtered, args.out)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
