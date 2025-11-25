#!/usr/bin/env python3
"""
Filter JaCoCo coverage report to show only source files (excluding test files).
"""
import xml.etree.ElementTree as ET
import argparse
import os
from pathlib import Path


def filter_jacoco(jacoco_xml, srcdir, output_html):
    """Filter JaCoCo XML report and generate HTML output."""
    
    # Parse the JaCoCo XML report
    tree = ET.parse(jacoco_xml)
    root = tree.getroot()
    
    # Get list of test files to determine which classes have tests
    test_dir = Path(srcdir)
    test_files = set()
    if test_dir.exists():
        for test_file in test_dir.glob('*Test.java'):
            # Extract the base class name (remove 'Test.java' suffix)
            base_name = test_file.stem.replace('Test', '')
            test_files.add(base_name + '.java')
    
    # Get all packages
    packages = root.findall('.//package')
    
    total_covered = 0
    total_missed = 0
    
    html_content = """<!DOCTYPE html>
<html>
<head>
    <title>Filtered JaCoCo Coverage Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .coverage { text-align: right; font-weight: bold; }
        .high { color: green; }
        .medium { color: orange; }
        .low { color: red; }
    </style>
</head>
<body>
    <h1>JaCoCo Coverage Report (Source Files Only)</h1>
"""
    
    html_content += "<table><tr><th>Class</th><th>Line Coverage</th><th>Branch Coverage</th></tr>"
    
    # Filter source files (exclude test directory)
    srcdir_path = Path(srcdir)
    
    for package in packages:
        package_name = package.get('name')
        
        for sourcefile in package.findall('.//sourcefile'):
            filename = sourcefile.get('name')
            
            # Skip if this is a test file
            if 'Test' in filename:
                continue
            
            # Only include classes that have a corresponding test file
            if filename not in test_files:
                continue
            
            # Get coverage counters
            line_counter = sourcefile.find(".//counter[@type='LINE']")
            branch_counter = sourcefile.find(".//counter[@type='BRANCH']")
            
            if line_counter is not None:
                missed = int(line_counter.get('missed', 0))
                covered = int(line_counter.get('covered', 0))
                total = missed + covered
                
                total_missed += missed
                total_covered += covered
                
                if total > 0:
                    line_pct = (covered / total) * 100
                    color_class = 'high' if line_pct >= 80 else ('medium' if line_pct >= 60 else 'low')
                    
                    branch_pct = 0
                    if branch_counter is not None:
                        b_missed = int(branch_counter.get('missed', 0))
                        b_covered = int(branch_counter.get('covered', 0))
                        b_total = b_missed + b_covered
                        if b_total > 0:
                            branch_pct = (b_covered / b_total) * 100
                    
                    html_content += f"""
                    <tr>
                        <td>{package_name.replace('/', '.')}/{filename}</td>
                        <td class='coverage {color_class}'>{line_pct:.1f}% ({covered}/{total})</td>
                        <td class='coverage'>{branch_pct:.1f}%</td>
                    </tr>
                    """
    
    # Add total
    grand_total = total_missed + total_covered
    if grand_total > 0:
        total_pct = (total_covered / grand_total) * 100
        color_class = 'high' if total_pct >= 80 else ('medium' if total_pct >= 60 else 'low')
        
        html_content += f"""
        <tr style='font-weight: bold; background-color: #e0e0e0;'>
            <td>TOTAL</td>
            <td class='coverage {color_class}'>{total_pct:.1f}% ({total_covered}/{grand_total})</td>
            <td class='coverage'>-</td>
        </tr>
        """
    
    html_content += "</table></body></html>"
    
    # Write output
    with open(output_html, 'w') as f:
        f.write(html_content)
    
    print(f"✓ Filtered coverage report generated: {output_html}")
    print(f"✓ Total coverage: {total_pct:.1f}% ({total_covered}/{grand_total} lines)")


def main():
    parser = argparse.ArgumentParser(description='Filter JaCoCo coverage report')
    parser.add_argument('--jacoco', required=True, help='Path to jacoco.xml')
    parser.add_argument('--srcdir', required=True, help='Source directory to filter')
    parser.add_argument('--out', required=True, help='Output HTML file')
    
    args = parser.parse_args()
    
    if not os.path.exists(args.jacoco):
        print(f"Error: JaCoCo XML file not found: {args.jacoco}")
        return 1
    
    filter_jacoco(args.jacoco, args.srcdir, args.out)
    return 0


if __name__ == '__main__':
    exit(main())
