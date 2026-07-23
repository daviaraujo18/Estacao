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

**Atualização (R.2 — especialização `Presenca::`/`Admin::ApplicationController`):** ao reativar o módulo administrativo (que depende de renderização implícita — actions sem `render` explícito), surgiram mais 3 lacunas do modo API não cobertas pela lista acima:
1. **`protect_from_forgery` nunca é chamado automaticamente.** Incluir `ActionController::RequestForgeryProtection` só disponibiliza o módulo — sem chamar `protect_from_forgery with: :exception` explicitamente, o callback `:verify_authenticity_token` nunca é registrado, e qualquer `skip_before_action :verify_authenticity_token` em um controller filho falha com `ArgumentError` (esse erro estava mascarado por outro `ArgumentError` anterior — `require_login` indefinido — que interrompia a definição da classe antes de chegar nessa linha).
2. **`allow_forgery_protection` não herda de `config.action_controller.allow_forgery_protection`** quando a base é `ActionController::API` (só `ActionController::Base` herda essa config automaticamente do Railtie). É preciso replicar manualmente: `self.allow_forgery_protection = Rails.application.config.action_controller.allow_forgery_protection != false` — senão a proteção CSRF fica sempre ativa, inclusive em `test.rb` onde deveria estar desligada, quebrando testes de integração que não enviam token.
3. **`config.api_only = true` exclui `:new`/`:edit` das rotas padrão de `resources`** (comportamento documentado de `ActionDispatch::Routing::Mapper::Resources`, não é bug) — qualquer módulo com formulários HTML sob um app `api_only` precisa declarar essas rotas explicitamente (`get ... as: :new_x` / `as: :edit_x`).
4. **`ActionController::API` usa `BasicImplicitRender`** (sempre `head :no_content` quando a action não chama `render`), não `ImplicitRender` (que busca template automaticamente como em `ActionController::Base`). Sem `include ActionController::ImplicitRender`, qualquer action sem `render`/`redirect_to` explícito devolve 204 vazio mesmo com a view existindo no caminho certo.

**Lição:** ao herdar de `ActionController::API` para reaproveitar renderização de views HTML "como se fosse Base", trate a lista de gaps como não-exaustiva — cada novo padrão de uso (CSRF skip, resources com formulário, action sem render explícito) pode revelar mais um módulo/comportamento que `ActionController::Base` dá de graça e `API` não. Validar com a suíte de testes completa após qualquer mudança em `ApplicationController`, não assumir que a lista de módulos incluídos já é suficiente.

---

### 22/07/2026 — `iteration_A.md` desatualizado em relação ao git log (status de tarefa "Pendente" já implementada)

**Contexto:** Tarefa A.11 — a tarefa foi declarada dependente de A.3 (`⬜ Pendente` no `iteration_A.md`)
**Problema:** O `git log` do branch `feature/sprint-a` mostra o commit `3d1ebf3 feat: integra PunchTypeService no SincronizarRegistrosPonto (A.3)` já presente e o código do `SincronizarRegistrosPontoController` já chama `PunchTypeService.determine` — ou seja, A.3 está de fato implementada no código, mas o `iteration_A.md` não foi atualizado para refletir isso.
**Solução:** Não presumir o status real de uma dependência apenas pelo checkbox do `iteration_N.md`; sempre cruzar com `git log --oneline` e o código-fonte do arquivo dependente antes de decidir se uma tarefa está de fato bloqueada. A.11 foi implementada normalmente (view/controller), pois o gap era apenas documental.
**Lição:** Checkboxes de `iteration_N.md` podem ficar dessincronizados do código real; ao identificar essa divergência, sinalizar explicitamente no relatório e na Linha do Tempo da tarefa em vez de silenciosamente assumir um dos dois como verdade.

---
