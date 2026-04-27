/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        brandBlue: '#E6F0FA',
        brandAccent: '#0EA5E9',
        ambulanceRed: '#E11D48',
        surfaceLight: 'rgba(255, 255, 255, 0.7)'
      },
      keyframes: {
        'fade-in-up': {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        'float': {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-15px)' },
        },
        'float-delayed': {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-20px)' },
        },
        'float-slow': {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        }
      },
      animation: {
        'fade-in-up': 'fade-in-up 0.5s ease-out forwards',
        'float': 'float 4s ease-in-out infinite',
        'float-delayed': 'float-delayed 5s ease-in-out infinite 1.5s',
        'float-slow': 'float-slow 6s ease-in-out infinite 0.5s',
      }
    },
  },
  plugins: [],
}
