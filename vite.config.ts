import {defineConfig} from "vite";
import {sveltekit} from "@sveltejs/kit/vite";
import {getBabelOutputPlugin} from "@rollup/plugin-babel";
import path from "path";
import {fileURLToPath} from "node:url";
import legacy from "@rollup/plugin-legacy";
import inject from "@rollup/plugin-inject";
// import globals from "rollup-plugin-external-globals";

// node_modules/systemjs/dist/system.js
// const systemjs = path.resolve(__dirname, "node_modules", "systemjs", "dist");
const systemjs = fileURLToPath(
  new URL(
    "node_modules/systemjs/dist/system.js",
    import.meta.url
  )
);

export default defineConfig({
  plugins: [sveltekit()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  /*resolve: {
    alias: {
      "systemjs": systemjs
    }
  },*/
  optimizeDeps: {
    include: [systemjs]
  },
  build: {
    rollupOptions: {
      external: [systemjs],
      output: {
        globals: {
          [systemjs]: "System"
        }
      },
      plugins: [
        /*legacy({
          "node_modules/systemjs/dist/system.js": "System"
        }),*/
        inject({
          System: "System",
          "window.System": "System"
        }),
        getBabelOutputPlugin({
          configFile: path.resolve(__dirname, "babel.config.json")
        })
      ]
    }
  }
});
