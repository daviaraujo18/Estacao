# Lições Aprendidas

> Arquivo: `governance/lessons.md`
> Propósito: Registrar conhecimento operacional não coberto por iteration, quality, inception ou ADRs.

### 22/07/2026 (atualizado) — `ActionController::API` + renderização de views com layout

**Contexto:** Tarefas A.4/A.5/A.7 — Layout compartilhado e views HTML (branch `feature/sprint-a`)
**Problema original:** `render :inicializar_ponto` em API mode retornava body vazio.
**Solução original:** `File.read` + `ERB.new` + `render html: ...html_safe`.
**Descoberta (A.7):** `ActionController::API` não inclui módulos cruciais presentes em `ActionController::Base`:
- `ActionView::Layouts` — fornece o método de classe `layout` e suporte a layout
- `ActionController::Helpers` — permite `helper_method` para expor métodos às views
- `ActionController::MimeResponds` — `respond_to` e content-type negotiation
- `ActionController::Flash` — flash messages (`notice`, `alert`)
- `ActionController::RequestForgeryProtection` — `csrf_meta_tags`
- `ActionController::ContentSecurityPolicy` — `csp_meta_tag`
- `helper Importmap::ImportmapTagsHelper` — `javascript_importmap_tags` (não registrado em API mode)

**Solução final:** Incluir esses módulos no `ApplicationController`. Com eles, `render :action, layout:` funciona normalmente em API mode.

---

### 22/07/2026 — `iteration_A.md` desatualizado em relação ao git log (status de tarefa "Pendente" já implementada)

**Contexto:** Tarefa A.11 — a tarefa foi declarada dependente de A.3 (`⬜ Pendente` no `iteration_A.md`)
**Problema:** O `git log` do branch `feature/sprint-a` mostra o commit `3d1ebf3 feat: integra PunchTypeService no SincronizarRegistrosPonto (A.3)` já presente e o código do `SincronizarRegistrosPontoController` já chama `PunchTypeService.determine` — ou seja, A.3 está de fato implementada no código, mas o `iteration_A.md` não foi atualizado para refletir isso.
**Solução:** Não presumir o status real de uma dependência apenas pelo checkbox do `iteration_N.md`; sempre cruzar com `git log --oneline` e o código-fonte do arquivo dependente antes de decidir se uma tarefa está de fato bloqueada. A.11 foi implementada normalmente (view/controller), pois o gap era apenas documental.
**Lição:** Checkboxes de `iteration_N.md` podem ficar dessincronizados do código real; ao identificar essa divergência, sinalizar explicitamente no relatório e na Linha do Tempo da tarefa em vez de silenciosamente assumir um dos dois como verdade.

---
