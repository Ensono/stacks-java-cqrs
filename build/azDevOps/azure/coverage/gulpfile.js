const gulp = require("gulp");
const fs = require("node:fs/promises");
const path = require("node:path");

// For inlining images in CSS
const postcss = require("gulp-postcss");
const imageInliner = require("postcss-image-inliner");
const { inlineSource } = require("inline-source");

gulp.task("inline-css-images", function () {
  const opts = {
    assetPaths: ["./jacoco/jacoco-resources"],
  };

  const plugins = [imageInliner(opts)];

  return gulp
    .src("./jacoco/**/*.css")
    .pipe(postcss(plugins))
    .pipe(gulp.dest("./jacoco"));
});

// For inlining everything into the HTML files
async function collectHtmlFiles(directory) {
  const entries = await fs.readdir(directory, { withFileTypes: true });
  const files = await Promise.all(
    entries.map(async function (entry) {
      const fullPath = path.join(directory, entry.name);

      if (entry.isDirectory()) {
        return collectHtmlFiles(fullPath);
      }

      return entry.name.endsWith(".html") ? [fullPath] : [];
    }),
  );

  return files.flat();
}

gulp.task("inline-sources", async function () {
  const sourceRoot = path.resolve("./jacoco");
  const destinationRoot = path.resolve("./jacoco-inline");
  const htmlFiles = await collectHtmlFiles(sourceRoot);

  await Promise.all(
    htmlFiles.map(async function (htmlFile) {
      const relativePath = path.relative(sourceRoot, htmlFile);
      const destinationPath = path.join(destinationRoot, relativePath);
      const inlinedHtml = await inlineSource(htmlFile, {
        attribute: false,
        compress: false,
        rootpath: path.dirname(htmlFile),
      });

      await fs.mkdir(path.dirname(destinationPath), { recursive: true });
      await fs.writeFile(destinationPath, inlinedHtml);
    }),
  );
});
