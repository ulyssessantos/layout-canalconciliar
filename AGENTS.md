# AGENTS.md — Frontend Design Rules

You are working on this repo’s frontend. Follow these rules:

## UI quality bar
- Default to accessible UI (WCAG-ish): labels, aria where needed, focus states, keyboard nav.
- Responsive by default: mobile-first, fluid layouts, avoid fixed heights.
- Prefer simple, clean hierarchy: clear headings, whitespace, consistent spacing.
- Avoid generic “template-y” UIs. Use thoughtful composition and microcopy.

## Tech + conventions (edit to match your stack)
- Framework: (Angular 17+/19, React, etc.)
- Styling: (Tailwind / SCSS / CSS Modules / PrimeNG / Angular Material)
- Components: reuse existing design system components first.
- Icons: prefer existing icon set in repo.
- i18n: never hardcode strings if project uses i18n.

## Output expectations
- When implementing UI: include components + styles + tests (if repo has UI tests).
- Add a short checklist at the end: a11y, responsiveness, states, empty/loading/error.

## Don’t
- Don’t introduce new UI libraries without explicit need.
- Don’t change unrelated formatting.
