package com.gustavogenovese.servermensajeria.core;

import com.gustavogenovese.servermensajeria.entidades.Mensaje;
import com.gustavogenovese.servermensajeria.entidades.Usuario;
import com.gustavogenovese.servermensajeria.entidades.dtos.MensajeDTO;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gus
 */
@Service
@Transactional
public class ServicioMensajesImpl implements ServicioMensajes{

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @Autowired
    private DTOBinder binder;

    @Override
    public boolean enviarMensaje(String remitenteId, String destinatarioNombre, String mensaje) {
        Usuario remitente = servicioUsuarios.buscarUsuarioPorId(remitenteId);
        if (remitente == null){
            return false;
        }
        Usuario destinatario = servicioUsuarios.buscarUsuarioPorUsuario(destinatarioNombre);
        if (destinatario == null){
            return false;
        }

        Mensaje m = new Mensaje();
        m.setId(UUID.randomUUID().toString());
        m.setRemitente(remitente);
        m.setDestinatario(destinatario);
        m.setMensaje(mensaje);
        m.setFecha(new Date());

        sessionFactory.getCurrentSession().save(m);
        return true;
    }

    @Override
    public List<MensajeDTO> listarMensajesPara(String destinatarioId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Mensaje.class);
        criteria.createCriteria("destinatario").add(Restrictions.idEq(destinatarioId));
        criteria.addOrder(Order.desc("fecha"));
        List<Mensaje> mensajes =  criteria.list();
        List<MensajeDTO> ret = binder.bindFromBusinessObjectList(MensajeDTO.class, mensajes);
        return ret;
    }

    @Override
    public List<MensajeDTO> listarMensajesDe(String remitenteId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Mensaje.class);
        criteria.createCriteria("remitente").add(Restrictions.idEq(remitenteId));
        criteria.addOrder(Order.desc("fecha"));
        List<Mensaje> mensajes =  criteria.list();
        List<MensajeDTO> ret = binder.bindFromBusinessObjectList(MensajeDTO.class, mensajes);
        return ret;
    }

}
