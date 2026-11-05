# PetVet (PetCarteira)

> Carteirinha digital de saúde do pet + lembretes automáticos, para clínicas veterinárias e petshops.

## English summary

**PetVet is an early-stage B2B2C MVP.** Veterinary clinics and pet shops register pets and their health history, and the system is designed to send automatic reminders (vaccines, deworming, check-ups) to pet owners. The full documentation below is in Portuguese.

| | |
|---|---|
| **Works today** | Clinic sign-up (creates the clinic and its first staff user) · CRUD for clinics, staff and pets · staff login with JWT · landing page with plans · 46 automated backend tests running on H2 |
| **Not built yet** | WhatsApp reminders (no integration yet) · pet-owner login · profile/dashboard endpoints return mocked data |
| **Known blocking issues** | Two divergent role models · logout tokens are kept only in memory · `ddl-auto: update` · landing-page traction numbers are fictional placeholders |
| **Fixed (2026-11-05)** | Backend build · sign-up against the real schema · marketplace provider creation restricted to ADMIN · JWT secrets without defaults · CORS origins configurable · plans returned in a stable order |
| **Business status** | No customer discovery interviews yet; pricing is a placeholder |
| **Stack** | Java 21 + Spring Boot 3.5 (Spring Security, JPA) · MySQL 8 · React + Vite + TypeScript |

The issues are documented on purpose: the goal is an honest picture of the MVP's current state, not a demo that looks finished.

---

Este README documenta o **estado atual do MVP** (produto + tecnologia), a ideia de negócio por trás dele e o que ainda falta para ele funcionar de ponta a ponta. O objetivo é que qualquer pessoa — sócio, novo colaborador, investidor ou o próprio time em três meses — consiga entender o projeto sem precisar de explicação verbal.

---

## 1. A ideia

### O problema

Clínicas veterinárias e petshops pequenos/médios não têm um sistema estruturado para lembrar o tutor de voltar. Vacinas atrasam, vermífugos são esquecidos, retornos não acontecem — não porque o tutor não se importa, mas porque ninguém avisa no momento certo. O controle hoje, na prática, costuma ser: agenda de papel, WhatsApp manual disparado por alguém da equipe, ou nada.

Isso gera dois problemas visíveis:

- **Para a clínica**: menos retorno de clientes, menos receita recorrente, pet sem acompanhamento contínuo.
- **Para o tutor**: perde o controle do histórico de saúde do próprio pet (vacina, peso, exames, medicação) porque essa informação fica presa no papel ou na cabeça de alguém da clínica.

### A solução

Uma plataforma B2B2C:

**PetVet (nós) → Clínica/Petshop (parceiro) → Tutor do pet (consumidor final)**

1. A clínica cadastra o pet e o histórico de saúde no sistema.
2. O sistema calcula automaticamente a próxima data de vacina/vermífugo/retorno.
3. Um lembrete é agendado e enviado automaticamente por WhatsApp para o tutor.
4. O tutor confirma o retorno ou reagenda.
5. A clínica ganha um histórico de saúde digital do pet e retenção maior de clientes; o tutor ganha uma "carteirinha digital" sempre acessível.

### Por que é B2B2C e não B2C direto

Não vendemos diretamente para o tutor. Vendemos para a clínica, que já tem a base de tutores/pets. A clínica é ao mesmo tempo cliente pagante, canal de distribuição (ela que traz os tutores para dentro do sistema) e fonte de autoridade (o tutor confia mais em algo recomendado pela própria clínica do que em um app baixado por conta própria).

### O que ainda é hipótese, não fato

Nenhuma entrevista de descoberta com clínica ou petshop foi realizada até o momento. Tudo abaixo ainda não tem validação de campo:

- Se o problema (esquecimento de retorno) é realmente doloroso o suficiente para a clínica pagar por uma solução.
- Se a clínica está disposta a operar o cadastro (esforço de implementação) em troca do benefício.
- Se o tutor de fato interage com um lembrete de WhatsApp vindo de um sistema terceiro (não da clínica diretamente).
- Quanto alguém pagaria por isso (os valores atuais de plano na landing page são placeholders, não pesquisa de mercado).

---

## 2. Estado atual do produto (o que existe de verdade)

### O que funciona hoje

- Cadastro público de clínica: `POST /api/auth/register` cria a clínica e o primeiro usuário dela (papel `EMPLOYEE`)
- Cadastro de clínica, funcionário e pet (CRUD básico)
- Autenticação de funcionário de clínica via login/senha + JWT
- Landing page com apresentação do produto e planos

### O que **não** existe ainda, apesar de ser o core da proposta

- **O envio de lembrete via WhatsApp não está implementado.** Hoje é só uma frase de intenção — não há integração real com nenhuma API de WhatsApp Business.
- **O tutor (dono do pet) não tem login no sistema.** Só funcionário de clínica autentica. Ou seja, a metade "C" do B2B2C (o consumidor final acessando sua própria carteirinha) ainda não existe.
- Os endpoints de perfil/dashboard de usuário retornam dados fixos (mockados), não o que o usuário logado realmente tem.

### Bugs bloqueantes conhecidos (auditoria técnica)

- Existem dois modelos de "papel de usuário" divergentes no código: `User.role` (texto, usado de verdade) e o enum `UserRole` (desconectado).
- Tokens de logout ficam só em memória: reiniciar o servidor "reativa" tokens que deveriam estar revogados; não funciona com mais de uma instância rodando.
- `ddl-auto: update` ativo no Hibernate — não deve ser usado em produção.
- Números de tração exibidos na landing page (ex: "1.200+ pets ativos") são **fictícios**, usados como placeholder visual — precisam ser removidos ou deixados claramente como exemplo antes de qualquer uso externo.

### Corrigido em 2026-11-05

- O backend não compilava (`SC_TOO_MANY_REQUESTS` não existe no Servlet 6.0 do Spring Boot 3.5) e a suíte de testes também não; agora os dois compilam e os testes rodam com H2, sem MySQL.
- **Cadastro**: gravava `role = "USER"` (recusado pelo banco) e não vinculava `clinic_id` (obrigatório). Agora cria a clínica e o usuário na mesma transação, com papel `EMPLOYEE`. Não é `ADMIN` de propósito: no `AuthzService`, `ADMIN` acessa qualquer clínica.
- `POST /api/marketplace/providers` aceitava qualquer usuário logado; agora exige `ADMIN`.
- Os segredos de JWT tinham valor padrão no código; agora `JWT_SECRET` e `JWT_REFRESH_SECRET` são obrigatórios (mínimo de 32 bytes) e a aplicação não sobe sem eles.
- CORS fixo em `localhost`; agora as origens vêm de `CORS_ALLOWED_ORIGINS`.
- `/api/plans` devolvia os planos em ordem aleatória; agora a ordem é sempre FREE, PRO, PREMIUM.

### Estado visual

A landing page ainda não tem identidade visual própria (paleta padrão de projeto React recém-criado, sem cor de marca, sem animação de scroll). É funcional para leitura, não para apresentação a um parceiro/investidor no formato atual.

---

## 3. Stack técnica

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 (atualmente com testes de upgrade para 25) + Spring Boot 3.5 + Spring Data JPA + Spring Security |
| Banco de dados | MySQL 8 |
| Frontend | React + Vite + TypeScript |
| Comunicação (planejada, não implementada) | WhatsApp Business API via integração externa |

## 4. Estrutura do repositório

```
backend/    → API Java (controllers, services, models, security)
database/   → schema SQL e migrations do MySQL
frontend/   → aplicação React (landing page + telas)
docs/       → documentação técnica (arquitetura)
```

## 5. Como rodar localmente

```bash
# Banco de dados
# Rodar os scripts em database/mysql/schema/ contra uma instância MySQL 8

# Backend (Maven 3.9+ e JDK 21 ou mais novo)
cd backend
export JWT_SECRET="um-segredo-aleatorio-com-pelo-menos-32-bytes"
export JWT_REFRESH_SECRET="outro-segredo-aleatorio-com-pelo-menos-32-bytes"
mvn spring-boot:run

# Testes do backend (usam H2 em memória, não precisam de MySQL)
mvn test

# Frontend
cd frontend
npm install
npm run dev
```

| Variável | Obrigatória | Padrão | Para quê |
|---|---|---|---|
| `JWT_SECRET` | sim | — | Assina o token de acesso (mínimo de 32 bytes) |
| `JWT_REFRESH_SECRET` | sim | — | Assina o refresh token (mínimo de 32 bytes; use um valor diferente do anterior) |
| `CORS_ALLOWED_ORIGINS` | não | portas locais 5173 e 3000 | Origens do front-end, separadas por vírgula; `*` é recusado |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | não | MySQL local | Conexão com o banco |

Para gerar um segredo: `openssl rand -base64 48`.

## 6. Próximos passos recomendados

1. Persistir a revogação de tokens (banco ou Redis) e unificar os dois modelos de papel de usuário.
2. Implementar de fato a integração com WhatsApp — é o diferencial do produto e hoje não existe.
3. Adicionar autenticação para o tutor (pet owner), não só para funcionário de clínica.
4. Redesenhar a landing page com identidade visual própria (paleta de marca, interação de scroll) antes de mostrar a qualquer parceiro externo.
5. Validar a hipótese central com entrevistas reais em clínicas/petshops antes de continuar investindo em engenharia neste vertical.
