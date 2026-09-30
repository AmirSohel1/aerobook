const fs = require('fs');
const path = require('path');

const featuresDir = path.resolve(__dirname, '../src/app/features');
const forbiddenPatterns = [
  /\bmock[a-zA-Z0-9_]*\b/i,
  /\bdummy[a-zA-Z0-9_]*\b/i,
  /\bfake[a-zA-Z0-9_]*\b/i,
  /\bseedFallback[a-zA-Z0-9_]*\b/i,
  /\bof\s*\(\s*\[/
];

// Exceptions if any (comments or UI texts that are legitimate descriptive labels)
const allowedSubstrings = [];

let violations = 0;

function scanDir(dir) {
  if (!fs.existsSync(dir)) return;
  const entries = fs.readdirSync(dir, { withFileTypes: true });
  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      scanDir(fullPath);
    } else if (entry.isFile() && (entry.name.endsWith('.ts') || entry.name.endsWith('.html'))) {
      const content = fs.readFileSync(fullPath, 'utf8');
      const lines = content.split('\n');
      lines.forEach((line, index) => {
        // Skip comment lines
        const trimmed = line.trim();
        if (trimmed.startsWith('//') || trimmed.startsWith('/*') || trimmed.startsWith('*')) {
          return;
        }
        for (const pattern of forbiddenPatterns) {
          if (pattern.test(line)) {
            const relPath = path.relative(path.resolve(__dirname, '..'), fullPath);
            console.error(`❌ Mock data violation found in ${relPath}:${index + 1}`);
            console.error(`   Line: ${trimmed.substring(0, 120)}`);
            violations++;
            break;
          }
        }
      });
    }
  }
}

console.log('🔍 Scanning frontend/src/app/features for mock/dummy/fake patterns...');
scanDir(featuresDir);

if (violations > 0) {
  console.error(`\n❌ Failed: ${violations} mock/dummy/fake data occurrences detected in features!`);
  process.exit(1);
} else {
  console.log('✅ Clean: No mock data patterns found in features! All data is backed by real services.');
  process.exit(0);
}
