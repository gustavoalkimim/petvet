# PetVet — arquitetura MVP

## Visão

O MVP de PetVet foca em três fluxos:

1. Cadastro da clínica e dos usuários
2. Cadastro do pet e do histórico veterinário
3. Geração e envio de lembretes automáticos por WhatsApp

## Domínio principal

- Clínica
- Usuário da clínica
- Tutor
- Pet
- Histórico veterinário
- Lembrete
- Assinatura e cobrança

## Camadas

### Banco de dados

- MySQL
- tabelas com constraints e relacionamentos claros
- tabela de auditoria para rastrear alterações
- scripts de schema e seed

### Backend

- Spring Boot
- JPA / Hibernate
- Spring Security
- controladores REST
- serviços para domain logic

### Frontend

- React + Vite + TypeScript
- Context API para autenticação e dados compartilhados
- páginas para login, dashboard e gestão de pets

## Regras de negócio cruciais

- Usuário da clínica só pode acessar dados da própria clínica
- Tutor só pode ver seus pets
- Lembrete só é gerado quando há próxima ação de saúde
- Auditar toda alteração relevante
- O status do lembrete deve ser rastreável
