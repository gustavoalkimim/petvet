package com.petvet.platform;

import java.util.Map;

public class ProductPlanCatalog {

    private final Map<ProductPlanTier, ProductPlan> plans = Map.of(
            ProductPlanTier.FREE, new ProductPlan(
                    ProductPlanTier.FREE,
                    "Básico",
                    "Plano inicial para tutores e uso pessoal.",
                    3,
                    false,
                    java.util.List.of(
                            "Cadastro de até 3 pets",
                            "Lembretes básicos",
                            "Busca básica por pet shops",
                            "Perfil do pet com informações essenciais"
                    ),
                    java.util.List.of(
                            "Pets cadastrados",
                            "Lembretes pendentes",
                            "Visão de rotina básica"
                    )
            ),
            ProductPlanTier.PRO, new ProductPlan(
                    ProductPlanTier.PRO,
                    "Pro",
                    "Plano para pet shops, clínicas e tutores mais ativos.",
                    1000,
                    true,
                    java.util.List.of(
                            "Dashboard de métricas avançadas",
                            "Histórico completo do pet",
                            "Agendamento e lembretes automáticos",
                            "Marketplace de pet shops e clínicas",
                            "Mapeamento por região",
                            "Relatórios básicos de desempenho"
                    ),
                    java.util.List.of(
                            "Retenção e performance por unidade",
                            "Taxa de agendamento",
                            "Serviços mais procurados",
                            "Faturamento e recorrência"
                    )
            ),
            ProductPlanTier.PREMIUM, new ProductPlan(
                    ProductPlanTier.PREMIUM,
                    "Premium",
                    "Plano corporativo para operações multi-unidade e tomada de decisão estratégica.",
                    Integer.MAX_VALUE,
                    true,
                    java.util.List.of(
                            "Múltiplas unidades e colaboradores",
                            "Dashboard executivo",
                            "Métricas avançadas por loja",
                            "Marketplace premium e campanhas",
                            "Visão separada para dono e operação",
                            "Alertas de otimização e retenção"
                    ),
                    java.util.List.of(
                            "Faturamento consolidado",
                            "Taxa de conversão por unidade",
                            "Comparativo entre unidades",
                            "HUDs de otimização e retenção",
                            "Serviços com maior potencial"
                    )
            )
    );

    public ProductPlan getPlan(ProductPlanTier tier) {
        return plans.get(tier);
    }

    // Map.of não garante ordem de iteração; a ordem dos planos segue o enum (FREE, PRO, PREMIUM).
    public java.util.List<ProductPlan> getAllPlans() {
        return java.util.Arrays.stream(ProductPlanTier.values())
                .map(plans::get)
                .toList();
    }
}
