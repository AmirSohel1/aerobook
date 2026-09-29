/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ['selector', '[data-theme="dark"]'],
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      fontFamily: {
        sans: [
          '"Plus Jakarta Sans"',
          '-apple-system',
          'BlinkMacSystemFont',
          '"SF Pro Display"',
          '"Segoe UI"',
          'Roboto',
          'sans-serif'
        ]
      },
      colors: {
        bg: 'var(--bg)',
        surface: 'var(--surface)',
        solid: 'var(--solid)',
        ink: 'var(--text)',
        'ink-2': 'var(--text-2)',
        'ink-3': 'var(--text-3)',
        line: 'var(--line)',
        accent: 'var(--accent)',
        'accent-2': 'var(--accent-2)',
        'accent-soft': 'var(--accent-soft)',
        seg: 'var(--seg)',
        'seg-on': 'var(--seg-on)',
        ok: 'var(--green)',
        'ok-soft': 'var(--green-soft)',
        warn: 'var(--amber)',
        'warn-soft': 'var(--amber-soft)',
        bad: 'var(--red)',
        'bad-soft': 'var(--red-soft)',
        // Backward compatibility
        brand: {
          50: '#FAF5FF',
          100: '#F3E8FF',
          200: '#E9D5FF',
          300: '#D8B4FE',
          400: '#C084FC',
          500: '#A855F7',
          600: '#9333EA',
          700: '#7E22CE',
          800: '#6B21A8',
          900: '#581C87',
          950: '#3B0764'
        }
      },
      borderRadius: {
        card: '28px',
        tile: '18px'
      },
      boxShadow: {
        card: 'var(--shadow)'
      },
      backgroundImage: {
        brand: 'var(--grad)',
        hero: 'var(--hero)'
      },
      letterSpacing: {
        tightest: '-0.045em'
      }
    }
  },
  plugins: []
};
