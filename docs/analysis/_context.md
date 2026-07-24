# _context.md — analysis
> Gerado em: 24/07/2026 | Fontes: `docs/documentacao-estacao-ponto.md`, `docs/relatorio-interacao-presenca-estacao.md`, PRD, código-fonte | Palavras: ~240
> Atualizar quando: Novo domínio mapeado, nova entidade modelada, fluxo alterado.

## O que esta pasta contém
Modelagem de domínio, fluxos de dados, entidades, casos de uso e mapeamento de integração entre a API Rails e a Estação Ponto JavaFX.

## Pontos-chave para agentes

### Entidades
- **User** — frequentador/usuário. Autenticação bcrypt + DES legado. Campos: nome_completo, username (auto-gerado), password_digest, status, digitais_hash
- **TimeRecord** — registro de batida de ponto. Campos: raw_data, punched_at, authentication_mode, punch_type, punch_type_explicit

### Casos de Uso Implementados
- UC01: CRUD Usuários (Admin::UsersController + views)
- UC02: Login manual (ValidarFrequentador — DES + bcrypt)
- UC03: Download digitais (DynFrequentadoresEstacao — serialização `;` + `'`)
- UC04: Hash MD5 (DynHashFrequentadoresEstacao)
- UC05: Sincronizar horário (CarregaRelogioAtual)
- UC06: Sincronizar registros (SincronizarRegistrosPonto — POST criptografado)
- UC07: Iniciar sessão (InicializarPonto, IniciarPonto, PontoDePresenca)
- UC10: Heartbeat (AdicioneEstacao)
- UC11: PunchType auto-alternação (PunchTypeService)

### Casos de Uso Pendentes
- UC08: Upload logs (UploadFile)
- UC09: Auto-update (AtualizarEstacoes)
- UC12: Job processamento registros sincronizados

## Estado atual
- 13 endpoints presenca + 5 rotas admin implementados e roteados
- Content negotiation no SincronizarRegistrosPonto (texto puro legado vs. JSON rico)
- PunchTypeService com fallback para payload sem punch_type explícito

## Referências para aprofundamento
- Engenharia reversa da Estação → `docs/documentacao-estacao-ponto.md`
- Especificação endpoints → `docs/relatorio-interacao-presenca-estacao.md`