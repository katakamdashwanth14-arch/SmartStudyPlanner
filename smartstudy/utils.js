export const sanitizeHTML = (str) => {
    const temp = document.createElement('div');
    temp.textContent = str;
    return temp.innerHTML;
};

export const PRIORITY_COLORS = {
    'High': '#EF4444',
    'Medium': '#F59E0B',
    'Low': '#10B981'
};

export const hexToCSS = (hex) => {
    return hex;
};
