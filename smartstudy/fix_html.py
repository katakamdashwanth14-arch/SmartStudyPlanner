import os
import glob
import re

files = glob.glob('*.html')
for file in files:
    if file == 'index.html':
        continue
    
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 1. remove any leftover orphaned </label>\n    </div> that got left behind
    content = re.sub(r'<body data-theme="light">\s*</label>\s*</div>', '<body data-theme="light">', content)

    # 2. ensure sidebar toggle exists and looks exactly like before
    sidebar_toggle_html = """            <div class="theme-switch-wrapper mt-auto">
                <span>Theme</span>
                <label class="theme-switch" for="checkbox">
                    <input type="checkbox" id="checkbox" aria-label="Toggle dark mode">
                    <div class="slider round"></div>
                </label>
            </div>"""

    # If it's missing (I don't think it's missing, but if it is, add it before logout)
    if 'id="checkbox"' not in content:
        content = content.replace('<button id="logout-btn"', sidebar_toggle_html + '\n            <button id="logout-btn"')
        
    # Remove any floating toggle that might exist
    content = re.sub(r'<div class="theme-switch-wrapper floating.*?</div>\s*</div>', '', content, flags=re.DOTALL)
    
    with open(file, 'w', encoding='utf-8') as f:
        f.write(content)

print("done")
