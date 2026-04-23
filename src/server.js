const express = require("express");
const multer = require("multer");
const PizZip = require("pizzip");
const Docxtemplater = require("docxtemplater");
const expressionParser = require("docxtemplater/expressions.js");

const app = express();
const port = process.env.PORT || 3000;

const supportedExtensions = new Set(["docx", "pptx", "xlsx"]);

const mimeByExtension = {
  docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
  xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
};

const storage = multer.memoryStorage();
const upload = multer({
  storage,
  limits: {
    fileSize: 20 * 1024 * 1024
  }
});

app.use(express.json({ limit: "2mb" }));

function getExtension(filename = "") {
  const dot = filename.lastIndexOf(".");
  if (dot === -1) {
    return "";
  }
  return filename.slice(dot + 1).toLowerCase();
}

function getBaseName(filename = "arquivo") {
  const dot = filename.lastIndexOf(".");
  return dot > 0 ? filename.slice(0, dot) : filename;
}

function normalizeDataPayload(input) {
  if (!input) {
    throw new Error("Campo 'data' é obrigatório.");
  }

  if (typeof input === "object") {
    return input;
  }

  if (typeof input === "string") {
    try {
      return JSON.parse(input);
    } catch (_err) {
      throw new Error("Campo 'data' precisa ser um JSON válido.");
    }
  }

  throw new Error("Campo 'data' precisa ser um objeto JSON.");
}

function renderTemplate(buffer, data) {
  const zip = new PizZip(buffer);
  const doc = new Docxtemplater(zip, {
    paragraphLoop: true,
    linebreaks: true,
    parser: expressionParser,
    delimiters: {
      start: "{",
      end: "}"
    }
  });

  doc.render(data);

  return doc.getZip().generate({
    type: "nodebuffer",
    compression: "DEFLATE"
  });
}

app.get("/health", (_req, res) => {
  res.status(200).json({ status: "ok" });
});

app.post("/render", upload.single("template"), (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({
        error: "Arquivo de template é obrigatório (multipart field: template)."
      });
    }

    const extension = getExtension(req.file.originalname);
    if (!supportedExtensions.has(extension)) {
      return res.status(400).json({
        error: "Formato não suportado. Use DOCX, PPTX ou XLSX."
      });
    }

    const data = normalizeDataPayload(req.body.data || req.body);
    const outputName = req.body.outputName || `${getBaseName(req.file.originalname)}-rendered.${extension}`;

    const renderedBuffer = renderTemplate(req.file.buffer, data);

    res.setHeader("Content-Type", mimeByExtension[extension]);
    res.setHeader("Content-Disposition", `attachment; filename=\"${outputName}\"`);

    return res.status(200).send(renderedBuffer);
  } catch (error) {
    return res.status(422).json({
      error: "Erro ao processar template.",
      details: error.message
    });
  }
});

app.use((err, _req, res, _next) => {
  if (err instanceof multer.MulterError) {
    return res.status(400).json({
      error: "Erro de upload.",
      details: err.message
    });
  }

  return res.status(500).json({
    error: "Erro interno inesperado.",
    details: err.message
  });
});

app.listen(port, () => {
  // eslint-disable-next-line no-console
  console.log(`Template renderer ativo na porta ${port}`);
});
