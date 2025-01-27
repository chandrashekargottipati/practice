/** @type {import('tailwindcss').Config} */
export default {
          content: [
                    "./src/**/*.{html,js,jsx,ts,tsx}",
                    "../ui.apps/src/main/content/jcr_root/apps/**/*.html"
          ],
          theme: {
                    extend: {},
          },
          plugins: [],
          corePlugins: {
                    preflight: false
          }
};
