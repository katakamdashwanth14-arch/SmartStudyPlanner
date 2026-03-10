import os

def check_brackets(file):
    with open(file, 'r') as f:
        text = f.read()
    
    # Remove strings and regexes so they don't break simple bracket parsing
    import re
    text = re.sub(r'(["\'])(?:(?=(\\?))\2.)*?\1', '', text)
    text = re.sub(r'//.*', '', text)
    text = re.sub(r'/\*.*?\*/', '', text, flags=re.DOTALL)
    text = re.sub(r'`[^`]*`', '', text, flags=re.DOTALL)

    stack = []
    lines = text.split('\n')
    for i, line in enumerate(lines):
        for j, char in enumerate(line):
            if char in '{[(':
                stack.append((char, i+1))
            elif char in '}])':
                if not stack:
                    return f"Unmatched closing '{char}' at line {i+1}"
                top, lnum = stack.pop()
                pairs = {'{': '}', '[': ']', '(': ')'}
                if pairs[top] != char:
                    return f"Mismatched closing '{char}' at line {i+1}, expected '{pairs[top]}' to match line {lnum}"
    if stack:
        return f"Unclosed '{stack[-1][0]}' opened at line {stack[-1][1]}"
    return "OK"

print(check_brackets('script.js'))
