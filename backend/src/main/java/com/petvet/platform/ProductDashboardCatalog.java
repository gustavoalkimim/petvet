package com.petvet.platform;

import java.util.List;
import java.util.Map;

public class ProductDashboardCatalog {

    private final Map<UserRole, ProductDashboardConfig> roleDashboards = Map.of(
            UserRole.PET_OWNER, new ProductDashboardConfig(
                    "Visão do tutor",
                    List.of("Pets ativos", "Vacinas próximas", "Lembretes pendentes"),
                    List.of("Rotina do pet", "Saúde e lembretes", "Busca por serviços locais"),
                    false
            ),
            UserRole.PET_SHOP, new ProductDashboardConfig(
                    "Operação do pet shop",
                    List.of("Clientes ativos", "Retenção por serviço", "Pedidos e agendamentos"),
                    List.of("Catálogo de serviços", "Métricas de fluxo", "Atendimento local"),
                    true
            ),
            UserRole.CLINIC_OWNER, new ProductDashboardConfig(
                    "Operação da clínica",
                    List.of("Retenção por pet", "Agendamentos do mês", "Faturamento por unidade"),
                    List.of("Agenda e vacinações", "Histórico clínico", "Métricas de conversão"),
                    true
            ),
            UserRole.COMPANY_OWNER, new ProductDashboardConfig(
                    "Visão executiva",
                    List.of("Faturamento consolidado", "Comparativo entre unidades", "Taxa de crescimento"),
                    List.of("HUDs de otimização", "Retenção e risco", "Campanhas e performance"),
                    true
            )
    );

    public ProductDashboardConfig forUser(UserPlanProfile profile) {
        ProductDashboardConfig dashboard = roleDashboards.get(profile.getRole());
        if (dashboard == null) {
            return new ProductDashboardConfig(
                    "Visão padrão",
                    List.of("Atividade geral", "Resumo do cliente"),
                    List.of("Cadastros", "Visão operacional"),
                    false
            );
        }

        if (profile.getPlanTier() == ProductPlanTier.PREMIUM && profile.getRole() == UserRole.COMPANY_OWNER) {
            return new ProductDashboardConfig(
                    "Visão executiva premium",
                    List.of("Faturamento consolidado", "Retenção por unidade", "Otimização e lucro"),
                    List.of("HUDs premium", "Foco em expansão", "Campanhas por unidade"),
                    true
            );
        }

        return dashboard;
    }
}
