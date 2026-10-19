# Java Web Scraping

A Java/Jsoup academic scraper that extracts scientific article metadata to CSV.

**Author:** Mohammed Alaa Eddine GUERBOUZ  
**University:** University of Ghardaia, Department of Mathematics and Computer Science  
**Academic period:** 2023–2025 master coursework where confirmed; some items have unverified dates  
**Portfolio curated:** 2026

## What this work demonstrates

This repository preserves practical assignments and projects completed during the academic programme, reorganized by problem rather than original archive names. It contains actual submitted code and reports, enhanced notebook introductions and descriptive folder names. It is not a claim of recent creation or independent production deployment.

## Project catalog

### undated · projects

| Work | Problem addressed |
|---|---|
| [Scientific Article Metadata Scraper](projects/undated/scientific-article-scraper/README.md) | Collect a small set of scientific article attributes (title, authors, publication date, DOI, abstract) to a CSV. |

## Running the Java source

The original `Book.java` and `WebScraper.java` are in `projects/undated/scientific-article-scraper/src/main/java` and unchanged copies are under `java-original/`. A new Maven POM was added for an easier local build:

```powershell
cd projects/undated/scientific-article-scraper
mvn compile
mvn exec:java
```

This scraper contacts a third-party site. Ensure access is permitted by its terms and robots.txt, do not overload servers, and expect HTML selectors to require maintenance. The Java code has not been verified against the current external website. The original archive also had a published C# ClickOnce binary **without C# source files**; this is held only in the full portfolio preservation bundle, not claimed here as source code.


## Notes about the original sources

- Original notebook **code cells were not refactored or rewritten**. A clearly labeled Markdown introduction was added to notebooks for provenance and readability.
- Original DOCX reports and supporting images were only renamed and moved; their content remains byte-for-byte identical.
- Some notebooks rely on missing datasets, local Windows paths, network downloads, old libraries or GPU resources and **have not been rerun or benchmarked**. No unverified accuracies are claimed.
- Notebook years in the original headers are usually **Master 1 2023–2024** or **Master 2 2024–2025**. Where the period is inferred from a neighboring numbered TP series or not explicit in a particular source notebook, the project README says so. Other dates are sourced from the original notebook headers.
- Review licenses/redistribution rights for third-party datasets before pushing publicly. Large binary datasets, generated artifacts and serialized R workspaces should not be committed without review.

For original filename-to-new-path tracking, see `PORTFOLIO_INVENTORY.csv` at the top level of the consolidated download (not included in this individual repository).
