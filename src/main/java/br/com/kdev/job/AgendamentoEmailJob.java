package br.com.kdev.job;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

import br.com.kdev.entidade.AgendamentoEmail;
import br.com.kdev.servico.AgendamentoEmailServico;

@Singleton
public class AgendamentoEmailJob {

	@Inject
	private AgendamentoEmailServico agendamentoEmailServico;
	
	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	private JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/EmailQueue")
	private Queue queue;
	
	@Schedule(hour = "*", minute = "*", second = "*/10")
	public synchronized void enviarEmail() {
		List<AgendamentoEmail> listarPorNaoAgendado = agendamentoEmailServico.listarPorNaoAgendado();
		
		listarPorNaoAgendado.forEach( emailNaoAgendado -> {
			context.createProducer().send(queue, emailNaoAgendado);
			//agendamentoEmailServico.enviar(emailNaoAgendado);
			agendamentoEmailServico.alterar(emailNaoAgendado);
		});
	}
}
