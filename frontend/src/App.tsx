import React, { useState, useEffect } from 'react';

const features = [
  {
    title: 'Agenda inteligente',
    text: 'Controle de vacina, remédios e retornos com lembretes automáticos para cada pet.'
  },
  {
    title: 'Histórico clínico',
    text: 'Toda a evolução do animal em um único painel, com registros claros e acessíveis.'
  },
  {
    title: 'Comunicação integrada',
    text: 'Mensagens automáticas por WhatsApp e alertas focados em retenção e confiança.'
  }
];

const metrics = [
  { value: '1.200+', label: 'Pets ativos' },
  { value: '96%', label: 'Lembretes confirmados' },
  { value: '4.8/5', label: 'Satisfação' }
];

const steps = [
  'Cadastro do tutor e pet',
  'Agendamento e histórico',
  'Lembretes e retenção ativa'
];

const fallbackPlans = [
  {
    name: 'Básico',
    price: 'Grátis',
    description: 'Para tutores que querem começar com organização e rotina leve.',
    accent: 'basic',
    features: [
      'Até 3 pets cadastrados',
      'Lembretes básicos de saúde',
      'Busca simples por pet shops',
      'Perfil do pet com informações essenciais'
    ],
    badge: 'Plano inicial'
  },
  {
    name: 'Pro',
    price: 'R$ 49/mês',
    description: 'Para clínicas, pet shops e tutores com uso frequente e gestão ativa.',
    accent: 'pro',
    features: [
      'Até 1.000 pets',
      'Marketplace e mapa por região',
      'Agenda, histórico e métricas',
      'Dashboards de retenção e agendamento'
    ],
    badge: 'Mais usado'
  },
  {
    name: 'Premium',
    price: 'Sob consulta',
    description: 'Para operação multi-unidade, gestão premium e visão executiva do negócio.',
    accent: 'premium',
    features: [
      'Unidades e colaboradores múltiplos',
      'Métricas executivas por loja',
      'HUDs de otimização e retenção',
      'Controle financeiro e campanhas premium'
    ],
    badge: 'Executivo'
  }
];

function App() {
  const [plans, setPlans] = useState(fallbackPlans);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPlans = async () => {
      try {
        const response = await fetch('/api/plans');
        if (response.ok) {
          const data = await response.json();
          const plansByTier = data.reduce((acc: any, plan: any) => {
            const tierName = plan.tier === 'FREE' ? 'Básico' : plan.tier === 'PRO' ? 'Pro' : 'Premium';
            const tierAccent = plan.tier === 'FREE' ? 'basic' : plan.tier === 'PRO' ? 'pro' : 'premium';
            const tierBadge = plan.tier === 'FREE' ? 'Plano inicial' : plan.tier === 'PRO' ? 'Mais usado' : 'Executivo';
            const tierPrice = plan.tier === 'FREE' ? 'Grátis' : plan.tier === 'PRO' ? 'R$ 49/mês' : 'Sob consulta';

            acc.push({
              name: tierName,
              price: tierPrice,
              description: plan.description,
              accent: tierAccent,
              features: plan.features,
              badge: tierBadge
            });
            return acc;
          }, []);
          setPlans(plansByTier);
        }
      } catch (error) {
        console.warn('Falha ao carregar planos do backend, usando dados padrão', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPlans();
  }, []);
  return (
    <main className="app-shell" id="top">
      <header className="topbar">
        <a href="#top" className="brand" aria-label="PetVet início">
          <span className="brand-mark">P</span>
          <span>PetVet</span>
        </a>

        <nav className="main-nav" aria-label="Navegação principal">
          <a href="#features">Funcionalidades</a>
          <a href="#workflow">Como funciona</a>
          <a href="#pricing">Preços</a>
        </nav>

        <div className="topbar-actions">
          <a href="#login" className="text-link">Login</a>
          <a href="#signup" className="primary-btn">Criar conta</a>
        </div>
      </header>

      <section className="hero" aria-labelledby="hero-title">
        <div className="hero-copy">
          <span className="badge">Operação em tempo real</span>
          <h1 id="hero-title">Saúde do pet organizada, clara e fácil de acompanhar.</h1>
          <p>
            A PetVet conecta clínica, tutor e rotina do animal em uma experiência simples,
            moderna e confiável para reduzir faltas e aumentar a retenção.
          </p>

          <div className="cta-row">
            <a href="#signup" className="primary-btn">Quero testar</a>
            <a href="#features" className="secondary-btn">Ver recursos</a>
          </div>

          <ul className="trust-list" aria-label="Principais diferenciais da plataforma">
            <li>Vacinas e retorno</li>
            <li>Histórico centralizado</li>
            <li>Alertas por WhatsApp</li>
          </ul>
        </div>

        <div className="hero-panel" aria-label="Resumo do painel PetVet">
          <div className="panel-card panel-card-primary">
            <span>Próxima vacina</span>
            <strong>V10 · 12/09</strong>
            <small>Luna • Shih Tzu</small>
          </div>

          <div className="panel-card panel-card-soft">
            <span>Lembretes enviados</span>
            <strong>18 este mês</strong>
            <small>WhatsApp + app</small>
          </div>

          <div className="panel-card panel-card-mini">
            <div>
              <span>Taxa de confirmação</span>
              <strong>96%</strong>
            </div>
            <small>Comparado com 71% no modelo manual</small>
          </div>
        </div>
      </section>

      <section className="stats-grid" aria-label="Métricas principais da plataforma">
        {metrics.map(({ value, label }) => (
          <article key={label} className="stat-card">
            <strong>{value}</strong>
            <span>{label}</span>
          </article>
        ))}
      </section>

      <section id="features" className="feature-section" aria-labelledby="features-title">
        <div className="section-heading">
          <span className="section-kicker">Funcionalidades</span>
          <h2 id="features-title">Tudo o que a rotina da clínica precisa para evoluir.</h2>
        </div>

        <div className="feature-grid">
          {features.map(({ title, text }) => (
            <article key={title} className="feature-card">
              <div className="feature-icon" aria-hidden="true">✓</div>
              <h3>{title}</h3>
              <p>{text}</p>
            </article>
          ))}
        </div>
      </section>

      <section id="workflow" className="workflow-section" aria-labelledby="workflow-title">
        <div className="section-heading narrow">
          <span className="section-kicker">Como funciona</span>
          <h2 id="workflow-title">Uma operação simples, do cadastro até o cuidado contínuo.</h2>
        </div>

        <div className="step-grid">
          {steps.map((step, index) => (
            <article key={step} className="step-card">
              <span className="step-index">0{index + 1}</span>
              <h3>{step}</h3>
              <p>
                A plataforma cuida da rotina com organização, visibilidade e lembretes
                automáticos para cada caso.
              </p>
            </article>
          ))}
        </div>
      </section>

      <section id="pricing" className="plans-section" aria-labelledby="pricing-title">
        <div className="section-heading narrow">
          <span className="section-kicker">Planos</span>
          <h2 id="pricing-title">Três níveis para atender tutores, pet shops e operação completa.</h2>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <p>Carregando planos...</p>
          </div>
        ) : (
          <div className="plans-grid">
            {plans.map(({ name, price, description, accent, features, badge }) => (
              <article key={name} className={`plan-card ${accent}`}>
                <div className="plan-header">
                  <span className="plan-badge">{badge}</span>
                  <h3>{name}</h3>
                </div>

                <div className="plan-price">{price}</div>
                <p className="plan-description">{description}</p>

                <ul className="plan-features">
                  {features.map((feature) => (
                    <li key={feature}>{feature}</li>
                  ))}
                </ul>

                <a href="#signup" className="primary-btn plan-button">Escolher {name}</a>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="cta-strip" aria-labelledby="signup-title">
        <div>
          <span className="section-kicker">Acelere sua operação</span>
          <h2 id="signup-title">Uma solução pensada para clínicas que querem crescer com confiança.</h2>
        </div>
        <a href="#signup" className="primary-btn">Solicitar demonstração</a>
      </section>
    </main>
  );
}

export default App;
