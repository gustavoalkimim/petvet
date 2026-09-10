# PetVet (PetCarteira)

> Carteirinha digital de saúde do pet + lembretes automáticos, para clínicas veterinárias e petshops.

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

- Cadastro de clínica, funcionário e pet (CRUD básico)
- Autenticação de funcionário de clínica via login/senha + JWT
- Landing page com apresentação do produto e planos

### O que **não** existe ainda, apesar de ser o core da proposta

- **O envio de lembrete via WhatsApp não está implementado.** Hoje é só uma frase de intenção — não há integração real com nenhuma API de WhatsApp Business.
- **O tutor (dono do pet) não tem login no sistema.** Só funcionário de clínica autentica. Ou seja, a metade "C" do B2B2C (o consumidor final acessando sua própria carteirinha) ainda não existe.
- Os endpoints de perfil/dashboard de usuário retornam dados fixos (mockados), não o que o usuário logado realmente tem.

### Bugs bloqueantes conhecidos (auditoria técnica)

- **Cadastro de usuário quebra contra o banco real**: o backend grava `role = "USER"`, mas o banco só aceita `ADMIN`, `EMPLOYEE` ou `SUPPORT`. Isso impede qualquer cadastro novo.
- O mesmo cadastro não vincula `clinic_id`, que é obrigatório no banco — segundo erro garantido no mesmo fluxo.
- Existem dois modelos de "papel de usuário" divergentes no código (um usado de verdade, outro desconectado/morto).
- `POST /api/marketplace/providers` não exige autenticação — qualquer um pode criar um prestador de serviço no marketplace.
- Tokens de logout ficam só em memória: reiniciar o servidor "reativa" tokens que deveriam estar revogados; não funciona com mais de uma instância rodando.
- Segredos de JWT com valor padrão fixo no código-fonte (risco se a variável de ambiente não for configurada em produção).
- `ddl-auto: update` ativo no Hibernate — não deve ser usado em produção.
- Números de tração exibidos na landing page (ex: "1.200+ pets ativos") são **fictícios**, usados como placeholder visual — precisam ser removidos ou deixados claramente como exemplo antes de qualquer uso externo.
- CORS liberado apenas para `localhost` — não funciona fora do ambiente local.

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

# Backend
cd backend
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

> Atenção: com os bugs de cadastro listados acima, o fluxo de criar uma nova conta/clínica **vai falhar** contra um banco MySQL real até que a inconsistência de `role`/`clinic_id` seja corrigida.

## 6. Próximos passos recomendados

1. Corrigir os bugs bloqueantes de cadastro (role/clinic_id) antes de qualquer teste com usuário real.
2. Implementar de fato a integração com WhatsApp — é o diferencial do produto e hoje não existe.
3. Adicionar autenticação para o tutor (pet owner), não só para funcionário de clínica.
4. Redesenhar a landing page com identidade visual própria (paleta de marca, interação de scroll) antes de mostrar a qualquer parceiro externo.
5. Validar a hipótese central com entrevistas reais em clínicas/petshops antes de continuar investindo em engenharia neste vertical.
