import adapter from "@sveltejs/adapter-static";
import {vitePreprocess} from "@sveltejs/kit/vite";

const svelteDir = "src/main/webapp";
const appDir = `${svelteDir}/app`;

/** @type {import("@sveltejs/kit").Config} */
const config = {
  // Consult https://kit.svelte.dev/docs/integrations#preprocessors
  // for more information about preprocessors
  preprocess: vitePreprocess(),

  kit: {
    adapter: adapter({
      pages: "frontend/build",
      fallback: "index.html",
      precompress: true
    }),
    prerender: { entries: [] },
    appDir: "_app",
    files: {
      assets: `${svelteDir}/static`,
      hooks: {
        client: `${appDir}/hooks.client`,
        server: `${appDir}/hooks.server`
      },
      lib: `${appDir}/lib`,
      params: `${appDir}/params`,
      routes: `${appDir}/routes`,
      serviceWorker: `${appDir}/service-worker`,
      appTemplate: `${appDir}/app.html`,
      errorTemplate: `${appDir}/error.html`
    }
  }
};

export default config;
