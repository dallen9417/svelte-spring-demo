import {defineConfig} from "vite";
import {sveltekit} from "@sveltejs/kit/vite";
import legacy from "@vitejs/plugin-legacy";
import type {Plugin} from "vite";
import * as path from "path";

export default defineConfig({
  plugins: [
    sveltekit(),
    legacy(),
    mutateConfig()
  ],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});

function mutateConfig(): Plugin[] {
  const finalBundle = {};
  const css = [];
  return [
    {
      name: "mutate-config-resolved",
      enforce: "post",
      apply: "build",
      configResolved(config) {
        const rollupOptions = config.build.rollupOptions;
        if (Array.isArray(rollupOptions.output)) {
          rollupOptions.output =
            rollupOptions.output.find(o => o.format === "system");
        }
      }
    },
    {
      name: "mutate-config-render-chunk",
      enforce: "pre",
      apply: "build",
      outputOptions: {
        order: "pre",
        // sequential: true,
        handler(options) {
          return;
        }
      }
    },
    {
      name: "mutate-config-generate-bundle",
      enforce: "pre",
      apply: "build",
      generateBundle: {
        order: "pre",
        handler(options, bundle) {
          if (options.format === "system") {
            Object.keys(bundle).filter(k => k.includes("asset")).forEach(k => {
              finalBundle[k] = bundle[k];
            });
          }
        }
      }
    },
    {
      name: "mutate-config-write-bundle",
      enforce: "pre",
      apply: "build",
      writeBundle: {
        order: "pre",
        sequential: true,
        handler(options) {
          if (options.format === "system") {
            Object.keys(finalBundle).forEach(k => {
              this.emitFile(finalBundle[k]);
            });
          }
        }
      }
    }];
}
