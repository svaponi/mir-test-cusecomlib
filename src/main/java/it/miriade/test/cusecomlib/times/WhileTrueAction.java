package it.miriade.test.cusecomlib.times;

import it.miriade.test.cusecomlib.CuseUtil;

/**
 * Un oggetto {@link WhileTrueAction} è usato nel polling per testare una azione nel tempo. Questa zione verrà ripetuta
 * fintantochè {@link #isTrue()} tornarà TRUE. <br/>
 * Questi oggetti possono essere ussati anche in altri ambiti per incapsulare una funzione dentro un oggetto, ovvero è
 * un workaround per permettere il passaggio di funzioni in Java.
 * Refactoring della classe Proc di Ruby (<a href="https://ruby-doc.org/core-2.2.0/Proc.html">documentazione
 * online</a>). <br/>
 * 
 * @see CuseUtil#polling(WhileTrueAction, Object...)
 * @author svaponi
 */
public interface WhileTrueAction {

	public boolean isTrue();
}