# _context.md — analysis
> Gerado em: 22/07/2026 | Fontes: `docs/documentacao-estacao-ponto.md`, `docs/relatorio-interacao-presenca-estacao.md`, Sessão Brainstorm (22/07/2026) | Palavras: ~250
> Atualizar quando: Novo domínio mapeado, nova entidade modelada, fluxo alterado.

## O que esta pasta contém
Modelagem de domínio, fluxos de dados, entidades, casos de uso e mapeamento de integração entre a API Rails (`api-ponto`) e a Estação Ponto JavaFX.

## Pontos-chave para agentes

### Entidades Principais
- **User** (model Rails) — frequentador/usuário. Autenticação via bcrypt + DES legado.
- **TimeRecord** (model Rails) — registro de batida de ponto. Recebido via `SincronizarRegistrosPonto`.
- **Frequentador** — entidade do domínio (ainda sem model próprio; representada na VIEW SQL `presenca_frequentadorestacao`).

### Casos de Uco (Implementados)
- UC02: Login manual (ValidarFrequentador) — DES + bcrypt.
- UC03: Download digitais (DynFrequentadoresEstacao) — serialização `;` + `'`.
- UC04: Hash MD5 (DynHashFrequentadoresEstacao) — controle versão.
- UC05: Sincronizar relógio (CarregaRelogioAtual) — timestamp servidor.
- UC06: Sincronizar registros offline (SincronizarRegistrosPonto) — POST criptografado.
- UC10: Heartbeat (AdicioneEstacao) — keepalive estação.

### Casos de Uso (Pendentes)
- UC01: CRUD Frequentador (listar, criar, editar, visualizar, inativar).
- UC07: Iniciar sessão ponto (InicializarPonto).
- UC08: Upload logs (UploadFile).
- UC09: Auto-update (AtualizarEstacoes).
- UC12: Job processamento registros sincronizados.

### Fluxo de Integração
Estação JavaFX → WebView carrega JSP/HTML → JavaScript dispara `alert('comando')` → OnAlertListener → Operacao enum → serviço Java → HTTP GET para API Rails.

## Estado atual
- 10 endpoints mapeados do Módulo Presença original, 11 controllers criados no Rails.
- Formato serialização legado: campos separados por `;`, registros por `'`.
- Criptografia compartilhada: DES/CBC/PKCS5Padding com chave `"cryp:gpf"`.

## Referências para aprofundamento
- Para engenharia reversa completa da Estação → `docs/documentacao-estacao-ponto.md`
- Para especificação detalhada de cada endpoint e fluxo → `docs/relatorio-interacao-presenca-estacao.md`
